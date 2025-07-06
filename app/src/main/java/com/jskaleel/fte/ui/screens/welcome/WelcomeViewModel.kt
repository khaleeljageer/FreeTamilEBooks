package com.jskaleel.fte.ui.screens.welcome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.domain.usecase.WelcomeUseCase
import com.jskaleel.fte.ui.utils.mutableNavigationState
import com.jskaleel.fte.ui.utils.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val useCase: WelcomeUseCase
) : ViewModel() {

    var navigation by mutableNavigationState<WelcomeNavigationState>()
        private set

    private val viewModelState = MutableStateFlow(WelcomeViewModelState())

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            if (useCase.getWelcomeShown().first()) {
                delay(SCREEN_DELAY)
                navigation = navigate(WelcomeNavigationState.Next)
            } else {
                delay(SCREEN_DELAY)
                viewModelState.update {
                    it.copy(loading = false)
                }
            }
        }
    }

    fun onEvent(event: WelcomeEvent) {
        when (event) {
            WelcomeEvent.NextClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    useCase.setWelcomeShown()
                }
                navigation = navigate(WelcomeNavigationState.Next)
            }
        }
    }

    companion object {
        private const val SCREEN_DELAY = 1500L
    }
}

private data class WelcomeViewModelState(
    val loading: Boolean = true,
) {
    fun toUiState(): WelcomeUiState {
        return if (loading) {
            WelcomeUiState.Loading
        } else {
            WelcomeUiState.Success
        }
    }
}

sealed interface WelcomeUiState {
    data object Loading : WelcomeUiState
    data object Success : WelcomeUiState
}

sealed interface WelcomeNavigationState {
    data object Next : WelcomeNavigationState
}

sealed interface WelcomeEvent {
    data object NextClicked : WelcomeEvent
}
