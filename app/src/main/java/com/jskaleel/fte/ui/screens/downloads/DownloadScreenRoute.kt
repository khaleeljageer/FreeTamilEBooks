package com.jskaleel.fte.ui.screens.downloads

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epubreader.android.EPubReader
import com.jskaleel.fte.core.model.listen
import com.jskaleel.fte.core.utils.CallBack
import kotlinx.coroutines.launch

@Composable
fun DownloadScreenRoute(
    addBook: CallBack,
    viewModel: DownloadViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    viewModel.navigation.listen {
        when (it) {
            is DownloadNavState.OpenBook -> {
                coroutine.launch {
                    launchReaderActivity(context)
                }
            }
        }
    }

    DownloadScreen(
        uiState = uiState,
        onRemove = viewModel::itemRemoved,
        onBookClick = viewModel::onBookClick,
        addBook = addBook,
    )
}

suspend fun launchReaderActivity(context: Context) {
    EPubReader.getReader().openBook(3L)
}

