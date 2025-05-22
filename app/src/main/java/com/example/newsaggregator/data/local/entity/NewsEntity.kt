package com.example.newsaggregator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val guid: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String?,
    val pubDate: String,
    val categories: String,
    val isFavorite: Boolean = false
)