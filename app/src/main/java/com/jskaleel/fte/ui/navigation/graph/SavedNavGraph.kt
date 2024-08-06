package com.jskaleel.fte.ui.navigation.graph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jskaleel.fte.ui.navigation.Route
import com.jskaleel.fte.ui.navigation.Screen
import com.jskaleel.fte.ui.screens.downloads.DownloadScreenRoute

fun NavGraphBuilder.savedNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = Screen.Saved.route,
        route = Route.Saved.name
    ) {
        composable(route = Screen.Saved.route) {
            DownloadScreenRoute(
                openBook = {},
                addBook = {
                    navController.popBackStack()
                },
                viewModel = hiltViewModel()
            )
        }
    }
}