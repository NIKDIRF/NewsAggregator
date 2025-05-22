package com.example.newsaggregator.domain.usecase

import com.example.newsaggregator.domain.repository.NewsRepository

class FetchAndCacheNewsUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke() = repository.fetchAndCacheNews()
}