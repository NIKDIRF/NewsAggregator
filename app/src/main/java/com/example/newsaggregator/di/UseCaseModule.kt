package com.example.newsaggregator.di

import com.example.newsaggregator.domain.repository.NewsRepository
import com.example.newsaggregator.domain.usecase.FetchAndCacheNewsUseCase
import com.example.newsaggregator.domain.usecase.GetCachedNewsUseCase
import com.example.newsaggregator.domain.usecase.GetFavoritesUseCase
import com.example.newsaggregator.domain.usecase.ToggleFavoriteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetCachedNewsUseCase(repository: NewsRepository): GetCachedNewsUseCase =
        GetCachedNewsUseCase(repository)

    @Provides
    @Singleton
    fun provideFetchAndCacheNewsUseCase(repository: NewsRepository): FetchAndCacheNewsUseCase =
        FetchAndCacheNewsUseCase(repository)

    @Provides
    @Singleton
    fun provideToggleFavoriteUseCase(repository: NewsRepository): ToggleFavoriteUseCase =
        ToggleFavoriteUseCase(repository)

    @Provides
    @Singleton
    fun provideGetFavoritesUseCase(repository: NewsRepository): GetFavoritesUseCase =
        GetFavoritesUseCase(repository)
}