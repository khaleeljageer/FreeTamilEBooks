package com.jskaleel.fte.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jskaleel.fte.ui.navigation.NavigationHost
import com.jskaleel.fte.ui.theme.FTEBooksTheme
import com.jskaleel.fte.ui.utils.BottomNavigationBar
import com.jskaleel.fte.ui.utils.ContentAwareTopAppBar
import com.jskaleel.fte.ui.utils.SnackBarControllerProvider
import com.jskaleel.fte.ui.utils.bottomBarItems
import com.jskaleel.fte.ui.utils.topBarItems
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            FTEBooksTheme {
                MainNavigation()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = bottomBarItems.any { it.route in currentRoute.orEmpty() }
    val showTopAppBar = topBarItems.any { it.route in currentRoute.orEmpty() }
    SnackBarControllerProvider { host ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AnimatedVisibility(
                    visible = showTopAppBar,
                    enter = fadeIn() + slideInVertically(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    ContentAwareTopAppBar(
                        navController = navController,
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = host) {
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        snackbarData = it
                    )
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = fadeIn() + slideInVertically(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    BottomNavigationBar(
                        items = bottomBarItems,
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                NavigationHost(
                    navController = navController,
                )
            }
        }
    }
}
