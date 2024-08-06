package com.jskaleel.fte.ui.navigation

sealed class Route(val name: String) {
    object Home : Route("Route_Home")
    object Saved : Route("Route_Saved")
    object Settings : Route("Route_Settings")
}