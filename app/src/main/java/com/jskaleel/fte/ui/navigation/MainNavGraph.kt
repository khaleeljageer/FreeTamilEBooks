package com.jskaleel.fte.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.jskaleel.fte.ui.screens.main.bookshelf.BookShelfRoute
import com.jskaleel.fte.ui.screens.main.bookshelf.BookShelfViewModel

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    navigation(
        startDestination = Screen.Main.BookShelf.route,
        route = Route.Main.name
    ) {
        animatedComposable(route = Screen.Main.BookShelf.route) {
            val viewModel: BookShelfViewModel = hiltViewModel()

            BookShelfRoute(
                openBook = {},
                viewModel = viewModel
            )
        }
        animatedComposable(route = Screen.Main.Search.route) {

        }

        animatedComposable(route = Screen.Main.Download.route) {
//            val viewModel: DownloadViewModel = hiltViewModel()
//
//            DownloadScreenRoute(
//                openBook = { id ->
//                    navController.navigate(
//                        Screen.PdfReader.Nav.create(bookId = id)
//                    )
//                },
//                viewModel = viewModel
//            )
        }

        animatedComposable(route = Screen.Main.About.route) {
//            AboutScreenRoute()
        }
    }
}
