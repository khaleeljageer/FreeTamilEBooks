package com.jskaleel.fte.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions

@Composable
fun rememberFteAppState(
    navController: NavHostController = rememberNavController(),
): FteAppState {
    return remember(navController) { FteAppState(navController) }
}

@Stable
class FteAppState(
    val navController: NavHostController,
) {
    val topLevelDestinations: List<BottomMenuItems> = BottomMenuItems.values().asList()
    private val bottomMenuItems = topLevelDestinations.map { it.route }

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination
    val shouldShowBottomBar: Boolean
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination?.route in bottomMenuItems

    fun navigateToTopLevelDestination(topLevelDestination: BottomMenuItems) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        navController.navigate(topLevelDestination.route, topLevelNavOptions)
    }
}