package com.jskaleel.fte.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination
import com.jskaleel.fte.core.model.getImagePainter

@Composable
fun FteBottomBar(
    bottomMenuItems: List<BottomMenuItems>,
    onNavigateToDestination: (BottomMenuItems) -> Unit,
    currentDestination: NavDestination?,
) {
    FteNavigationBar {
        bottomMenuItems.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            FteNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    val icon = if (selected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }
                    Icon(
                        painter = icon.getImagePainter(),
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = destination.iconTextId),
                        fontWeight = if (selected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                },
            )
        }
    }
}