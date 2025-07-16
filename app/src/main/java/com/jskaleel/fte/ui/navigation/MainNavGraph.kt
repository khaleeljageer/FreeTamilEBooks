package com.jskaleel.fte.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.jskaleel.fte.ui.screens.main.about.AboutScreenRoute
import com.jskaleel.fte.ui.screens.main.about.AboutViewModel
import com.jskaleel.fte.ui.screens.main.about.webpage.WebpageScreenRoute
import com.jskaleel.fte.ui.screens.main.bookshelf.BookShelfRoute
import com.jskaleel.fte.ui.screens.main.bookshelf.BookShelfViewModel
import com.jskaleel.fte.ui.screens.main.downloads.DownloadScreenRoute
import com.jskaleel.fte.ui.screens.main.downloads.DownloadViewModel
import com.jskaleel.fte.ui.screens.main.search.SearchScreenRoute
import com.jskaleel.fte.ui.screens.main.search.SearchViewModel

fun NavGraphBuilder.mainNavGraph(
    navController: NavController
) {
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
            val viewModel: SearchViewModel = hiltViewModel()
            SearchScreenRoute(viewModel = viewModel)
        }

        animatedComposable(route = Screen.Main.Download.route) {
            val viewModel: DownloadViewModel = hiltViewModel()

            DownloadScreenRoute(
                viewModel = viewModel
            )
        }

        animatedComposable(route = Screen.Main.About.route) {
            val viewModel: AboutViewModel = hiltViewModel()

            AboutScreenRoute(
                openHtml = { title, path ->
                    navController.navigate(
                        route = Screen.Webpage.LinkOne.create(
                            title = title,
                            path = path
                        )
                    )
                },
                viewModel = viewModel
            )
        }

        animatedComposable(route = Screen.Webpage.LinkOne.link) {
            val args = Screen.Webpage.LinkOne.get(it.arguments)
            WebpageScreenRoute(
                onBack = {
                    navController.popBackStack()
                },
                title = args.title,
                url = args.path
            )
        }
    }
}
