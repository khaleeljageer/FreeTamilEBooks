package com.jskaleel.fte.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.core.model.ThemeConfig
import com.jskaleel.fte.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {
    private val viewModelState = MutableStateFlow(SettingsViewModelState())

    val uiState = viewModelState.map {
        it.toUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = viewModelState.value.toUiState()
    )

    init {
        viewModelScope.launch {
            getUserThemePreference()
        }
    }

    private suspend fun getUserThemePreference() {
        settingsUseCase.getUserThemeData().collect { data ->
            viewModelState.update {
                it.copy(
                    themeConfig = data.themeConfig,
                )
            }
        }
    }

    fun onChangeDarkThemeConfig(themeConfig: ThemeConfig) {
        viewModelScope.launch {
            settingsUseCase.setThemeConfig(themeConfig = themeConfig)
            viewModelState.update {
                it.copy(themeConfig = themeConfig)
            }
        }
    }
}

private data class SettingsViewModelState(
    val loading: Boolean = true,
    val themeConfig: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
) {
    fun toUiState() = SettingsUiState(
        loading = loading, darkThemeConfig = themeConfig
    )
}

data class SettingsUiState(
    val loading: Boolean,
    val darkThemeConfig: ThemeConfig,
)