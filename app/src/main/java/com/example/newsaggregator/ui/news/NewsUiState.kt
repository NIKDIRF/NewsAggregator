package com.example.newsaggregator.ui.news

import com.example.newsaggregator.domain.model.NewsItem

data class NewsUiState(
    val searchQuery: String = "",
    val selectedTags: Set<String> = emptySet(),
    val isFavoritesOnly: Boolean = false,
    val filteredNews: List<NewsItem> = emptyList(),
    val allTags: List<String> = emptyList(),
    val isSearchMode: Boolean = false
)