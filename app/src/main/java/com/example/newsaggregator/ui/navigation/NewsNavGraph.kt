package com.example.newsaggregator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.newsaggregator.ui.news.NewsScreen
import com.example.newsaggregator.ui.news.NewsViewModel

@Composable
fun NewsNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "news") {
        composable("news") {
            val viewModel: NewsViewModel = hiltViewModel()
            NewsScreen(viewModel = viewModel)
        }
    }
}