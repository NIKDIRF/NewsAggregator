package com.example.newsaggregator.domain.usecase

import com.example.newsaggregator.domain.repository.NewsRepository

class ToggleFavoriteUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(guid: String) = repository.toggleFavorite(guid)
}