package com.jskaleel.fte.ui.screens.main.downloads

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.fte.R
import com.jskaleel.fte.core.StringCallBack
import com.jskaleel.fte.ui.screens.common.FullScreenLoader
import com.jskaleel.fte.ui.utils.ProvideAppBarTitle
import com.jskaleel.fte.ui.utils.consume

@Composable
fun DownloadScreenRoute(
    openBook: StringCallBack,
    viewModel: DownloadViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.navigation.consume { state ->
        when (state) {
            is DownloadNavigationState.OpenBook -> {
                openBook(state.id)
            }
        }
    }

    ProvideAppBarTitle {
        Text(
            text = stringResource(R.string.downloads),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
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
