package com.jskaleel.fte.ui.navigation.graph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jskaleel.fte.ui.navigation.Route
import com.jskaleel.fte.ui.navigation.Screen
import com.jskaleel.fte.ui.screens.home.HomeScreenRoute

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = Screen.Home.route,
        route = Route.Home.name
    ) {
        composable(route = Screen.Home.route) {
            HomeScreenRoute(
                openBook = {},
                viewModel = hiltViewModel()
            )
        }
    }
}