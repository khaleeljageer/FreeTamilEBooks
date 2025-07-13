package com.jskaleel.fte.ui.screens.main.about

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.fte.R
import com.jskaleel.fte.core.StringCallBack
import com.jskaleel.fte.ui.utils.ProvideAppBarTitle
import com.jskaleel.fte.ui.utils.consume

@Composable
fun AboutScreenRoute(
    openHtml: StringCallBack,
    viewModel: AboutViewModel
) {
    val uriHandler = LocalUriHandler.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.navigation.consume {
        when (it) {
            is AboutNavigationState.Email -> {

            }

            is AboutNavigationState.OpenHtml -> {
                openHtml(it.path)
            }

            is AboutNavigationState.OpenUrl -> {
                uriHandler.openUri(it.url)
            }
        }
    }

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