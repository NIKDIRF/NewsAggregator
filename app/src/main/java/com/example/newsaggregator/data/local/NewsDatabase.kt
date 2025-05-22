package com.example.newsaggregator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsaggregator.data.local.entity.NewsEntity

@Database(entities = [NewsEntity::class], version = 1)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}