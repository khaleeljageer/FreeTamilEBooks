package com.jskaleel.fte.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Saved : Screen("saved")
    object Settings : Screen("settings")
}