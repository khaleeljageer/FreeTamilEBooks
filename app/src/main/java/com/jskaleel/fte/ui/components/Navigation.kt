package com.jskaleel.fte.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.jskaleel.fte.R
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.ui.navigation.Screen


@Composable
fun FteNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = FteNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}

object FteNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}

@Composable
fun RowScope.FteNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = FteNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = FteNavigationDefaults.navigationContentColor(),
            selectedTextColor = FteNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = FteNavigationDefaults.navigationContentColor(),
            indicatorColor = FteNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

enum class BottomMenuItems(
    val selectedIcon: ImageType.ResourceImage,
    val unselectedIcon: ImageType.ResourceImage,
    val iconTextId: Int,
    val route: String,
) {
    HOME(
        selectedIcon = ImageType.ResourceImage(FteIcons.Home),
        unselectedIcon = ImageType.ResourceImage(FteIcons.HomeBorder),
        iconTextId = R.string.home,
        route = Screen.Home.route
    ),
    SAVED(
        selectedIcon = ImageType.ResourceImage(FteIcons.Saved),
        unselectedIcon = ImageType.ResourceImage(FteIcons.SavedBorder),
        iconTextId = R.string.saved,
        route = Screen.Saved.route
    ),
    SETTINGS(
        selectedIcon = ImageType.ResourceImage(FteIcons.Settings),
        unselectedIcon = ImageType.ResourceImage(FteIcons.SettingsBorder),
        iconTextId = R.string.settings,
        route = Screen.Settings.route
    ),
}

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: BottomMenuItems) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false