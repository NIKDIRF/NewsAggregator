package com.example.newsaggregator.ui.utils

import androidx.core.text.HtmlCompat

fun String.htmlToPlainText(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim()
}