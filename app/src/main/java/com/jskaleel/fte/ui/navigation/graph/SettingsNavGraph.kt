package com.jskaleel.fte.ui.navigation.graph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jskaleel.fte.ui.navigation.Route
import com.jskaleel.fte.ui.navigation.Screen
import com.jskaleel.fte.ui.screens.settings.SettingsScreenRoute

fun NavGraphBuilder.settingsNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = Screen.Settings.route,
        route = Route.Settings.name
    ) {
        composable(route = Screen.Settings.route) {
            SettingsScreenRoute(
                viewModel = hiltViewModel()
            )
        }
    }
}