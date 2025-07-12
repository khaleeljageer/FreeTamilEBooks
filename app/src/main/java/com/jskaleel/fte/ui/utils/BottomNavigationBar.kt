package com.jskaleel.fte.ui.utils

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CloudDownload
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
        unSelectedIcon = Icons.Rounded.Book,
        selectedIcon = Icons.Filled.Book,
        route = Screen.Main.BookShelf.route
    ),
    BottomBarItem(
        title = "Search",
        unSelectedIcon = Icons.Outlined.Search,
        selectedIcon = Icons.Filled.Search,
        route = Screen.Main.Search.route
    ),
    BottomBarItem(
        title = "Downloads",
        unSelectedIcon = Icons.Outlined.CloudDownload,
        selectedIcon = Icons.Filled.CloudDownload,
        route = Screen.Main.Download.route
    ),
    BottomBarItem(
        title = "About",
        unSelectedIcon = Icons.Outlined.Info,
        selectedIcon = Icons.Filled.Info,
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
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Crossfade(
                        targetState = selected,
                        animationSpec = tween(150),
                        label = ""
                    ) {
                        if (it) {
                            Icon(
                                imageVector = item.selectedIcon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                imageVector = item.unSelectedIcon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
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
