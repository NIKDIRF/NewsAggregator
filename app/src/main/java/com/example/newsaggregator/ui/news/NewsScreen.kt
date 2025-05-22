package com.example.newsaggregator.ui.news

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newsaggregator.domain.model.NewsItem
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.newsaggregator.ui.utils.htmlToPlainText
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(viewModel: NewsViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.errorMessage.collectAsState().value
    val context = LocalContext.current
    val isSearchMode = uiState.isSearchMode

    val listState = rememberLazyListState()

    LaunchedEffect(
        uiState.searchQuery,
        uiState.selectedTags,
        uiState.isFavoritesOnly
    ) {
        if (uiState.filteredNews.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            if (isSearchMode) {
                SearchPanel(
                    uiState = uiState,
                    onQueryChange = viewModel::updateSearchQuery,
                    onToggleFavoriteFilter = viewModel::toggleFavoritesOnly,
                    onTagClick = viewModel::toggleTag,
                    onRemoveFilterTag = viewModel::removeTag,
                    resetFiltersAndExitSearch = viewModel::resetFiltersAndExitSearch
                )
            } else {
                TopAppBar(
                    title = { Text("Guardian News") },
                    actions = {
                        IconButton(onClick = viewModel::enterSearchMode) {
                            Icon(Icons.Default.Search, contentDescription = "Поиск")
                        }
                    }
                )
            }
        }
    ) { padding ->

        SwipeRefresh(
            state = SwipeRefreshState(isRefreshing = isLoading),
            onRefresh = { viewModel.refreshNews() },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                when {
                    uiState.filteredNews.isNotEmpty() -> {
                        items(uiState.filteredNews) { item ->
                            NewsItemCard(
                                item = item,
                                selectedTags = uiState.selectedTags,
                                onToggleTag = {
                                    viewModel.toggleTag(it)
                                    viewModel.enterSearchMode()
                                },
                                onClick = {
                                    val intent = CustomTabsIntent.Builder().build()
                                    intent.launchUrl(context, item.url.toUri())
                                },
                                onToggleFavorite = {
                                    viewModel.toggleFavorite(item.guid)
                                }
                            )
                        }
                    }

                    error != null -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Error: $error")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsItemCard(
    item: NewsItem,
    selectedTags: Set<String>,
    onToggleTag: (String) -> Unit,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            item.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .size(coil.size.Size.ORIGINAL)
                        .build(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        modifier = Modifier.width(96.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Лайк",
                                tint = if (item.isFavorite) Color.Red else LocalContentColor.current
                            )
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, item.url)
                                }
                                context.startActivity(
                                    Intent.createChooser(intent, "Share via")
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Поделиться")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.description.htmlToPlainText(),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (item.categories.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(item.categories) { category ->
                            val isSelected = selectedTags.contains(category)

                            AssistChip(
                                onClick = { onToggleTag(category) },
                                label = {
                                    Text(
                                        category,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = if (isSelected)
                                    AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                else
                                    AssistChipDefaults.assistChipColors(),
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = item.pubDate,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}