package com.jskaleel.fte.ui.screens.home

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenRoute(
    openBook: (String) -> Unit,
    viewModel: HomeViewModel
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

    HomeScreen(
        uiState = uiState,
        onDownloadClick = viewModel::downloadBook,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearchClick = viewModel::onSearchClick,
        onSearchClear = viewModel::onSearchClear,
        onSearchActiveChange = viewModel::onSearchActiveChange,
    )
}