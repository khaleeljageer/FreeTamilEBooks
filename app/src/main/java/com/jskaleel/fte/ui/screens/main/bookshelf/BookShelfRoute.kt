package com.jskaleel.fte.ui.screens.main.bookshelf

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.jskaleel.fte.ui.screens.common.FullScreenLoader

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BookShelfRoute(
    openBook: (String) -> Unit,
    viewModel: BookShelfViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermission = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        )
        LaunchedEffect(key1 = true) {
            notificationPermission.launchPermissionRequest()
        }
    }

    when (val state = uiState) {
        BookShelfViewModelUiState.Loading -> FullScreenLoader()
        is BookShelfViewModelUiState.Success -> {
            BookListContent(
                searchQuery = state.searchQuery,
                searchActive = state.searchActive,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onSearchClick = viewModel::onSearchClick,
                onSearchClear = viewModel::onSearchClear,
                onSearchActiveChange = viewModel::onSearchActiveChange,
                onDownloadClick = viewModel::downloadBook,
                books = state.books,
                onSearchResultClick = viewModel::onSearchResultClick,
                searchList = state.searchList,
            )
        }
    }
}