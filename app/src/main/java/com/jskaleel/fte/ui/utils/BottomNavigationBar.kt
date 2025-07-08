package com.jskaleel.fte.ui.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jskaleel.fte.ui.model.BottomBarItem
import com.jskaleel.fte.ui.navigation.Route
import com.jskaleel.fte.ui.navigation.Screen

val bottomBarItems = listOf(
    BottomBarItem(
        title = "Books",
        icon = Icons.Rounded.Book,
        route = Screen.Main.BookShelf.route
    ),
    BottomBarItem(
        title = "Search",
        icon = Icons.Outlined.Search,
        route = Screen.Main.Search.route
    ),
    BottomBarItem(
        title = "Downloads",
        icon = Icons.Outlined.CloudDownload,
        route = Screen.Main.Download.route
    ),
    BottomBarItem(
        title = "About",
        icon = Icons.Outlined.Info,
        route = Screen.Main.About.route
    )
)

val bottomBarMenuRoutes = bottomBarItems.map { it.route }

@Composable
fun BottomNavigationBar(
    items: List<BottomBarItem>,
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                    )
                },
                label = null,
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == Route.Main.name) {
                        navController.popBackStack(Route.Main.name, inclusive = false)
                    }
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(Route.Main.name) {
                                saveState = true
                            }
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    indicatorColor = MaterialTheme.colorScheme.background,
                )
            )
        }
    }
}
