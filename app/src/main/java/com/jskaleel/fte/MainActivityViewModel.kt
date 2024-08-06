package com.jskaleel.fte

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.core.model.ThemeConfig
import com.jskaleel.fte.domain.usecase.MainActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val mainActivityUseCase: MainActivityUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MainActivityViewModelState())
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState(),
        )

    init {
        subscribeNetworkMonitor()
        getUserConfig()
    }

    private fun getUserConfig() {
        viewModelScope.launch {
            mainActivityUseCase.getUserConfig().collect { theme ->
                viewModelState.update {
                    it.copy(
                        themeConfig = theme.themeConfig,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun subscribeNetworkMonitor() {
        viewModelScope.launch {
            mainActivityUseCase.subscribeNetworkMonitor().collect { status ->
                viewModelState.update { it.copy(isOnline = status) }
            }
        }
    }
}

private data class MainActivityViewModelState(
    val isLoading: Boolean = true,
    val isOnline: Boolean = true,
    val themeConfig: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
) {
    fun toUiState() = MainActivityUiState(
        isLoading = isLoading,
        isOnline = isOnline,
        uiModel = MainActivityUiModel(
            themeConfig = themeConfig,
        )
    )
}

data class MainActivityUiState(
    val isLoading: Boolean = true,
    val isOnline: Boolean = true,
    val uiModel: MainActivityUiModel = MainActivityUiModel()
)