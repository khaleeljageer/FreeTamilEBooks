package com.jskaleel.fte.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun HomeScreenRoute(
    openBook: (String) -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        loading = uiState.loading,
        onDownloadClick = viewModel::downloadBook,
        books = uiState.books
    )
}