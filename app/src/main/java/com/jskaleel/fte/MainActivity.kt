package com.jskaleel.fte

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jskaleel.fte.core.model.ThemeConfig
import com.jskaleel.fte.ui.components.FteAppState
import com.jskaleel.fte.ui.components.rememberFteAppState
import com.jskaleel.fte.ui.screens.app.FteApp
import com.jskaleel.fte.ui.theme.FteTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var uiState: MainActivityUiState by mutableStateOf(
            MainActivityUiState()
        )
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach {
                        uiState = it
                    }
                    .collect()
            }
        }

        setContent {
            val appState: FteAppState = rememberFteAppState()

            FteTheme {
                FteApp(
                    isOnline = uiState.isOnline,
                    appState = appState
                )
            }
        }
    }
}

data class MainActivityUiModel(
    val themeConfig: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
)