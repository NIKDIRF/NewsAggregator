package com.example.newsaggregator.di

import com.example.newsaggregator.data.local.NewsDao
import com.example.newsaggregator.data.repository.NewsRepositoryImpl
import com.example.newsaggregator.data.rss.RssFeed
import com.example.newsaggregator.domain.repository.NewsRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.theguardian.com")
            .addConverterFactory(XML.asConverterFactory("application/xml".toMediaType()))
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    @Singleton
    fun provideRssFeed(retrofit: Retrofit): RssFeed =
        retrofit.create(RssFeed::class.java)

    @Provides
    @Singleton
    fun provideNewsRepository(
        rssFeed: RssFeed,
        newsDao: NewsDao
    ): NewsRepository = NewsRepositoryImpl(rssFeed, newsDao)
}