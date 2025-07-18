package com.jskaleel.fte.ui.screens.main.bookshelf

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.jskaleel.fte.core.launchReaderActivity
import com.jskaleel.fte.ui.screens.common.FullScreenLoader
import com.jskaleel.fte.ui.utils.SnackBarController
import com.jskaleel.fte.ui.utils.consume

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BookShelfRoute(
    openBook: (String) -> Unit,
    viewModel: BookShelfViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.navigation.consume {
        when (it) {
            is BookShelfNavigationState.OpenBook -> {
                context.launchReaderActivity(it.id)
            }
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermission = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        )
        LaunchedEffect(key1 = true) {
            notificationPermission.launchPermissionRequest()
        }
    }

    when (val state = uiState) {
        BookShelfUiState.Loading -> FullScreenLoader()
        is BookShelfUiState.Success -> {
            BookShelfContent(
                event = viewModel::onEvent,
                books = state.books,
                error = state.error,
            )
        }
    }
}