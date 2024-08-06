package com.jskaleel.fte.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SettingsScreenRoute(
    viewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        darkThemeConfig = uiState.darkThemeConfig,
        onChangeDarkThemeConfig = viewModel::onChangeDarkThemeConfig,
    )
}