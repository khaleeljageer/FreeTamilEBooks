package com.jskaleel.fte.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.jskaleel.fte.ui.navigation.graph.homeNavGraph
import com.jskaleel.fte.ui.navigation.graph.savedNavGraph
import com.jskaleel.fte.ui.navigation.graph.settingsNavGraph

@Composable
fun NavigationHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = Route.Home.name
    ) {
        homeNavGraph(navController = navHostController)
        savedNavGraph(navController = navHostController)
        settingsNavGraph(navController = navHostController)
    }
}