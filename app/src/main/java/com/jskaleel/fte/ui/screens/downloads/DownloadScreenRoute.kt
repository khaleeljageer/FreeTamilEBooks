package com.jskaleel.fte.ui.screens.downloads

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.fte.core.model.listen
import com.jskaleel.fte.core.utils.CallBack
import com.jskhaleel.reader.reader.ReaderActivityContract

@Composable
fun DownloadScreenRoute(
    addBook: CallBack,
    viewModel: DownloadViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.navigation.listen {
        when (it) {
            is DownloadNavState.OpenBook -> {
                launchReaderActivity(context)
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

fun launchReaderActivity(context: Context) {
    val intent = ReaderActivityContract().createIntent(
        context,
        ReaderActivityContract.Arguments(bookId = 123)
    )

    context.startActivity(intent)
}

