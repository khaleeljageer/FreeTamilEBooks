package com.jskaleel.fte.ui.screens.main.downloads

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.epub.reader.ReaderActivityContract
import com.jskaleel.fte.R
import com.jskaleel.fte.ui.screens.common.FullScreenLoader
import com.jskaleel.fte.ui.utils.ProvideAppBarTitle
import com.jskaleel.fte.ui.utils.consume

@Composable
fun DownloadScreenRoute(
    viewModel: DownloadViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.navigation.consume { state ->
        when (state) {
            is DownloadNavigationState.OpenBook -> {
                val intent = ReaderActivityContract().createIntent(
                    context,
                    ReaderActivityContract.Arguments(
                        bookId = state.id
                    )
                )
                context.startActivity(intent)
            }
        }
    }

    ProvideAppBarTitle {
        Text(
            text = stringResource(R.string.downloads),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp
            ),
        )
    }

    when (val state = uiState) {
        DownloadUiState.Loading -> FullScreenLoader()
        DownloadUiState.Empty -> DownloadEmptyScreen()
        is DownloadUiState.Success -> {
            DownloadScreenContent(
                event = viewModel::onEvent,
                books = state.books,
            )
        }
    }
}
