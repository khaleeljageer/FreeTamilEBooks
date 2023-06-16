package com.jskaleel.fte.ui.navigation.graph

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jskaleel.fte.ui.navigation.Route
import com.jskaleel.fte.ui.navigation.Screen

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = Screen.Home.route,
        route = Route.Home.name
    ) {
        composable(route = Screen.Home.route) {
            Text(text = "Home Screen", style = MaterialTheme.typography.headlineSmall)
        }
    }
}