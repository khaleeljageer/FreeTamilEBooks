package com.jskaleel.fte.ui.screens.downloads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.fte.core.utils.CallBack

@Composable
fun DownloadScreenRoute(
    openBook: (String) -> Unit,
    addBook: CallBack,
    viewModel: DownloadViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DownloadScreen(
        uiState = uiState,
        onRemove = viewModel::itemRemoved,
        addBook = addBook,
    )
}

