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
            private const val TITLE = "title"
            val link = "$route/{$TITLE}/{$PATH}"

            fun create(title: String, path: String): String {
                return "$route/$title/$path"
            }

            fun get(bundle: Bundle?): Args {
                val path = bundle?.getString(PATH) ?: ""
                val title = bundle?.getString(TITLE) ?: ""
                return Args(title, path)
            }

            data class Args(
                val title: String,
                val path: String
            )
        }
    }
}

sealed class Route(val name: String) {
    data object Welcome : Route(name = "Route_Welcome")
    data object Main : Route(name = "Route_Main")
}
