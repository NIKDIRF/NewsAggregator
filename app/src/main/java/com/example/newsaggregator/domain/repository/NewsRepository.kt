package com.example.newsaggregator.domain.repository

import com.example.newsaggregator.domain.model.NewsItem
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getCachedNews(): Flow<List<NewsItem>>
    suspend fun fetchAndCacheNews()
    suspend fun toggleFavorite(guid: String)
    fun getFavorites(): Flow<List<NewsItem>>
}