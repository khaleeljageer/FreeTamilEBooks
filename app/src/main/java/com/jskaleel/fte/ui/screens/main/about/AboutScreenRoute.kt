package com.jskaleel.fte.ui.screens.main.about

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.fte.R
import com.jskaleel.fte.ui.utils.ProvideAppBarTitle

@Composable
fun AboutScreenRoute(
    viewModel: AboutViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProvideAppBarTitle {
        Text(
            text = stringResource(R.string.about_us),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
        )
    }
    AboutScreenContent(
        menus = uiState.menus,
        onEvent = viewModel::onEvent
    )
}