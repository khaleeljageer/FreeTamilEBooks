package com.jskaleel.fte.ui.screens.main.search

import androidx.compose.runtime.Composable

@Composable
fun SearchScreenRoute(
    viewModel: SearchViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
}