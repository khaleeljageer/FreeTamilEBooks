package com.jskaleel.fte.ui.screens.main.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.fte.ui.screens.common.FullScreenLoader

@Composable
fun SearchScreenRoute(
    viewModel: SearchViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        SearchUiState.Loading -> FullScreenLoader()
        is SearchUiState.Success -> {
            SearchContent(
                onEvent = viewModel::onEvent,
                books = state.books,
                categories = state.categories,
                recentSearches = state.recentSearches,
            )
        }
    }
}