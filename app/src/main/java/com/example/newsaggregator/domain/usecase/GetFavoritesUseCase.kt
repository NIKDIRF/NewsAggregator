package com.example.newsaggregator.domain.usecase

import com.example.newsaggregator.domain.model.NewsItem
import com.example.newsaggregator.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetFavoritesUseCase(
    private val repository: NewsRepository
) {
    operator fun invoke(): Flow<List<NewsItem>> = repository.getFavorites()
}