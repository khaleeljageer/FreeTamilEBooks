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
        is SearchUiState.Success.DefaultResult -> {
            DefaultSearchContent(
                onEvent = viewModel::onEvent,
                categories = state.categories,
                recentReads = state.recentReads,
                active = state.active,
                searchQuery = state.searchQuery,
            )
        }

        is SearchUiState.Success.EmptySearchResult -> {
            EmptySearchResult(
                onEvent = viewModel::onEvent,
                active = state.active,
                searchQuery = state.searchQuery,
            )
        }

        is SearchUiState.Success.SearchResult -> {
            SearchResultContent(
                onEvent = viewModel::onEvent,
                books = state.books,
                active = state.active,
                searchQuery = state.searchQuery,
            )
        }
    }
}
