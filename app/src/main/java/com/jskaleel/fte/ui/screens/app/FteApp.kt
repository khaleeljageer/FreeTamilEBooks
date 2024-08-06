package com.jskaleel.fte.ui.screens.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jskaleel.fte.ui.components.FteAppState
import com.jskaleel.fte.ui.components.FteBackground
import com.jskaleel.fte.ui.components.FteBottomBar
import com.jskaleel.fte.ui.components.FteGradientBackground
import com.jskaleel.fte.ui.navigation.NavigationHost
import com.jskaleel.fte.ui.theme.LocalGradientColors

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class
)
@Composable
fun FteApp(
    isOnline: Boolean,
    appState: FteAppState
) {
    FteBackground {
        FteGradientBackground(
            gradientColors = LocalGradientColors.current,
        ) {
            val snackBarHostState = remember { SnackbarHostState() }
            LaunchedEffect(!isOnline) {
                if (!isOnline) {
                    snackBarHostState.showSnackbar(
                        message = "No internet connection",
                        duration = SnackbarDuration.Indefinite,
                    )
                }
            }
            Scaffold(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                snackbarHost = { SnackbarHost(snackBarHostState) },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    if (appState.shouldShowBottomBar) {
                        FteBottomBar(
                            bottomMenuItems = appState.topLevelDestinations,
                            onNavigateToDestination = appState::navigateToTopLevelDestination,
                            currentDestination = appState.currentDestination,
                        )
                    }
                },
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .consumeWindowInsets(padding)
                ) {
                    NavigationHost(navHostController = appState.navController)
                }
            }
        }
    }
}






