package com.example.newsaggregator.data.repository

import com.example.newsaggregator.data.local.NewsDao
import com.example.newsaggregator.data.mapper.toEntity
import com.example.newsaggregator.data.mapper.toNewsItem
import com.example.newsaggregator.data.rss.RssFeed
import com.example.newsaggregator.domain.model.NewsItem
import com.example.newsaggregator.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsRepositoryImpl(
    private val rssFeed: RssFeed,
    private val newsDao: NewsDao
) : NewsRepository {

    override fun getCachedNews(): Flow<List<NewsItem>> {
        return newsDao.getAllNews().map { list -> list.map { it.toNewsItem() } }
    }

    override suspend fun fetchAndCacheNews() {
        val favorites = newsDao.getFavoriteGuids().toSet()

        val items = rssFeed.getRss().channel.items.map { dto ->
            val item = dto.toNewsItem()
            item.copy(isFavorite = item.guid in favorites)
        }

        val entities = items.map { it.toEntity() }
        newsDao.insertAll(entities)
    }

    override suspend fun toggleFavorite(guid: String) {
        newsDao.toggleFavorite(guid)
    }

    override fun getFavorites(): Flow<List<NewsItem>> {
        return newsDao.getFavoriteNews().map { list -> list.map { it.toNewsItem() } }
    }
}