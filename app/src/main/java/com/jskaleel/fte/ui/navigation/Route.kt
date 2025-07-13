package com.jskaleel.fte.ui.navigation

import android.os.Bundle

sealed class Screen(val route: String) {
    object Welcome : Screen(route = "screen_welcome")

    object Main {
        data object BookShelf : Screen(route = "screen_main_book_shelf")
        data object Search : Screen(route = "screen_main_search")
        data object Download : Screen(route = "screen_main_download")
        data object About : Screen(route = "screen_main_about")
    }

    object Webpage : Screen(route = "screen_webpage") {
        object LinkOne {
            private const val PATH = "path"
            val link = "$route/{$PATH}"

            fun create(path: String): String {
                return "$route/$path"
            }

            fun get(bundle: Bundle?): String {
                return bundle?.getString(PATH) ?: ""
            }
        }
    }
}

sealed class Route(val name: String) {
    data object Welcome : Route(name = "Route_Welcome")
    data object Main : Route(name = "Route_Main")
}
