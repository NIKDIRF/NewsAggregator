package com.example.newsaggregator.domain.model

import java.util.Date

data class NewsItem(
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String?,
    val pubDate: String,
    val guid: String,
    val categories: List<String> = emptyList(),
    val pubDateParsed: Date? = null,
    val isFavorite: Boolean = false
)