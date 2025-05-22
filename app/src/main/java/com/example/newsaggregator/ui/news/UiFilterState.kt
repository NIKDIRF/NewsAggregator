package com.example.newsaggregator.ui.news

data class UiFilterState(
    val query: String = "",
    val selectedTags: Set<String> = emptySet(),
    val isFavoritesOnly: Boolean = false,
    val isSearchMode: Boolean = false
)