package com.jskaleel.fte.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreenRoute(
    openBook: (String) -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        HomeViewModelUiState.Loading -> BookListLoaderContent()
        is HomeViewModelUiState.Success -> BookListContent(
            onDownloadClick = viewModel::downloadBook,
            books = state.books
        )

        is HomeViewModelUiState.Error -> {
        }
    }
}