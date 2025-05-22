package com.example.newsaggregator.di

import android.content.Context
import androidx.room.Room
import com.example.newsaggregator.data.local.NewsDao
import com.example.newsaggregator.data.local.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NewsDatabase =
        Room.databaseBuilder(context, NewsDatabase::class.java, "news.db").build()

    @Provides
    fun provideNewsDao(database: NewsDatabase): NewsDao = database.newsDao()
}