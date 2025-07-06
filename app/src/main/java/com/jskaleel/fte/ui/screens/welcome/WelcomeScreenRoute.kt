package com.jskaleel.fte.ui.screens.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.ui.screens.common.FullScreenLoader
import com.jskaleel.fte.ui.utils.consume

@Composable
fun WelcomeScreenRoute(
    onNext: CallBack,
    viewModel: WelcomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.navigation.consume { state ->
        when (state) {
            WelcomeNavigationState.Next -> onNext()
        }
    }

    when (uiState) {
        WelcomeUiState.Loading -> FullScreenLoader()
        is WelcomeUiState.Success -> {
            WelcomeScreenContent(
                event = viewModel::onEvent
            )
        }
    }
}
