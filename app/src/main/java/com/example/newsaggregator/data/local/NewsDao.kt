package com.example.newsaggregator.data.local

import androidx.room.*
import com.example.newsaggregator.data.local.entity.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Query("SELECT * FROM news ORDER BY pubDate DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE isFavorite = 1 ORDER BY pubDate DESC")
    fun getFavoriteNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<NewsEntity>)

    @Query("UPDATE news SET isFavorite = NOT isFavorite WHERE guid = :guid")
    suspend fun toggleFavorite(guid: String)

    @Query("SELECT guid FROM news WHERE isFavorite = 1")
    suspend fun getFavoriteGuids(): List<String>
}