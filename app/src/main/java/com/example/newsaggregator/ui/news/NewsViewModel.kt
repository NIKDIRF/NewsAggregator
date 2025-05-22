package com.example.newsaggregator.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsaggregator.domain.model.NewsItem
import com.example.newsaggregator.domain.usecase.FetchAndCacheNewsUseCase
import com.example.newsaggregator.domain.usecase.GetCachedNewsUseCase
import com.example.newsaggregator.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    getCachedNewsUseCase: GetCachedNewsUseCase,
    private val fetchAndCacheNewsUseCase: FetchAndCacheNewsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())
    private val _isFavoritesOnly = MutableStateFlow(false)
    private val _isSearchMode = MutableStateFlow(false)
    private val _allTags = MutableStateFlow<List<String>>(emptyList())

    private val allNews: Flow<List<NewsItem>> = getCachedNewsUseCase().onEach { news ->
        _allTags.value = news.flatMap { it.categories }.distinct().sorted()
    }

    private val filterState: StateFlow<UiFilterState> = combine(
        _searchQuery,
        _selectedTags,
        _isFavoritesOnly,
        _isSearchMode
    ) { query, tags, favOnly, searchMode ->
        UiFilterState(query, tags, favOnly, searchMode)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiFilterState())

    val uiState: StateFlow<NewsUiState> = combine(
        allNews,
        _allTags,
        filterState
    ) { news, allTags, filter ->

        val filtered = news.filter { item ->
            val matchesQuery = filter.query.isBlank() ||
                    item.title.contains(filter.query, true) ||
                    item.description.contains(filter.query, true) ||
                    item.categories.any { it.contains(filter.query, true) }

            val matchesTags = filter.selectedTags.isEmpty() ||
                    item.categories.any { it in filter.selectedTags }

            val matchesFav = !filter.isFavoritesOnly || item.isFavorite

            matchesQuery && matchesTags && matchesFav
        }

        NewsUiState(
            searchQuery = filter.query,
            selectedTags = filter.selectedTags,
            isFavoritesOnly = filter.isFavoritesOnly,
            filteredNews = filtered,
            allTags = allTags,
            isSearchMode = filter.isSearchMode
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NewsUiState())

    init {
        refreshNews()
    }

    fun refreshNews() {
        viewModelScope.launch {
            val hasCachedNews = uiState.value.filteredNews.isNotEmpty()

            if (!hasCachedNews) _isLoading.value = true
            _errorMessage.value = null

            try {
                fetchAndCacheNewsUseCase()
            } catch (e: IOException) {
                if (!hasCachedNews) {
                    _errorMessage.value = "No internet connection. Try again later."
                }
            } catch (e: HttpException) {
                val code = e.response()?.code() ?: -1
                if (!hasCachedNews) {
                    _errorMessage.value = "Server error ($code). Try again later."
                }
            } catch (e: Exception) {
                if (!hasCachedNews) {
                    _errorMessage.value = "Unexpected error. Try again later."
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetFiltersAndExitSearch() {
        _searchQuery.value = ""
        _selectedTags.value = emptySet()
        _isFavoritesOnly.value = false
        _isSearchMode.value = false
    }

    fun toggleFavorite(guid: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(guid)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTag(tag: String) {
        _selectedTags.value = _selectedTags.value.toMutableSet().also {
            if (!it.add(tag)) it.remove(tag)
        }
    }

    fun removeTag(tag: String) {
        _selectedTags.value -= tag
    }

    fun toggleFavoritesOnly() {
        _isFavoritesOnly.value = !_isFavoritesOnly.value
    }

    fun enterSearchMode() {
        _isSearchMode.value = true
    }
}