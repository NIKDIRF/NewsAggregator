package com.example.newsaggregator.ui.news

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun SearchPanel(
    uiState: NewsUiState,
    onQueryChange: (String) -> Unit,
    onToggleFavoriteFilter: () -> Unit,
    onTagClick: (String) -> Unit,
    onRemoveFilterTag: (String) -> Unit,
    resetFiltersAndExitSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = "Поиск", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (uiState.searchQuery.isBlank()) {
                                Text("Search...", color = Color.Gray, fontSize = 18.sp)
                            }
                            innerTextField()
                        }
                    )
                }
            }

            IconButton(onClick = onToggleFavoriteFilter) {
                Icon(
                    imageVector = if (uiState.isFavoritesOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Избранное",
                    tint = if (uiState.isFavoritesOnly) Color.Red else LocalContentColor.current

                )
            }

            IconButton(onClick = resetFiltersAndExitSearch) {
                Icon(Icons.Default.Close, contentDescription = "Назад")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.searchQuery.isNotBlank()) {
            FlowRow(
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 72.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                uiState.allTags
                    .filter { it.contains(uiState.searchQuery, ignoreCase = true) }
                    .forEach { tag ->
                        AssistChip(
                            onClick = { onTagClick(tag) },
                            label = { Text(tag) }
                        )
                    }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.selectedTags.isNotEmpty()) {
            Text(text = "Selected tags:", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 48.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                uiState.selectedTags.forEach { tag ->
                    AssistChip(
                        onClick = { onRemoveFilterTag(tag) },
                        label = { Text(tag) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }
        }
    }
}