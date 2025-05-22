package com.example.newsaggregator.data.mapper

import android.util.Log
import com.example.newsaggregator.data.local.entity.NewsEntity
import com.example.newsaggregator.data.rss.dto.ItemDto
import com.example.newsaggregator.domain.model.NewsItem
import java.text.SimpleDateFormat
import java.util.Locale

private val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

fun ItemDto.toNewsItem(): NewsItem {
    val parsedDate = try {
        dateFormat.parse(pubDate)
    } catch (e: Exception) {
        null
    }

    return NewsItem(
        title = title,
        description = description,
        url = guid,
        imageUrl = contents.getOrNull(1)?.url ?: contents.firstOrNull()?.url,
        pubDate = pubDate,
        guid = guid,
        categories = categories.map { it.value },
        pubDateParsed = parsedDate,
        isFavorite = false
    )
}

fun NewsEntity.toNewsItem(): NewsItem {
    val parsedDate = try {
        dateFormat.parse(pubDate)
    } catch (e: Exception) {
        null
    }

    return NewsItem(
        title = title,
        description = description,
        url = url,
        imageUrl = imageUrl,
        pubDate = pubDate,
        guid = guid,
        categories = categories.split(";").filter { it.isNotBlank() },
        pubDateParsed = parsedDate,
        isFavorite = isFavorite
    )
}

fun NewsItem.toEntity(): NewsEntity {
    return NewsEntity(
        guid = guid,
        title = title,
        description = description,
        url = url,
        imageUrl = imageUrl,
        pubDate = pubDate,
        categories = categories.joinToString(";"),
        isFavorite = isFavorite
    )
}