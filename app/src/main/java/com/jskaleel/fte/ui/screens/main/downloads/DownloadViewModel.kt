package com.jskaleel.fte.ui.screens.main.downloads

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.usecase.BookShelfUseCase
import com.jskaleel.fte.ui.screens.main.downloads.DownloadNavigationState.OpenBook
import com.jskaleel.fte.ui.utils.mutableNavigationState
import com.jskaleel.fte.ui.utils.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val useCase: BookShelfUseCase,
) : ViewModel() {

    var navigation by mutableNavigationState<DownloadNavigationState>()
        private set

    private val viewModelState = MutableStateFlow(DownloadViewModelState())

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )

    fun onEvent(event: DownloadEvent) {
        when (event) {
            is DownloadEvent.OnBookClick -> {
                navigation = navigate(OpenBook(id = event.bookId))
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.observeDownloadedBooks().collect { books ->
                viewModelState.update {
                    it.copy(
                        loading = false,
                        books = books
                    )
                }
            }
        }
    }
}

private data class DownloadViewModelState(
    val loading: Boolean = true,
    val books: List<Book> = emptyList()
) {
    fun toUiState(): DownloadUiState {
        return if (loading) {
            DownloadUiState.Loading
        } else {
            if (books.isEmpty()) {
                DownloadUiState.Empty
            } else {
                DownloadUiState.Success(
                    books = books.map {
                        BookUiModel(
                            id = it.id,
                            title = it.title,
                            downloaded = it.downloaded,
                            author = it.author,
                            category = it.category,
                            image = it.image,
                        )
                    }
                )
            }
        }
    }
}

sealed interface DownloadUiState {
    data object Loading : DownloadUiState
    data object Empty : DownloadUiState
    data class Success(val books: List<BookUiModel>) : DownloadUiState
}

sealed interface DownloadNavigationState {
    data class OpenBook(val id: String) : DownloadNavigationState
}

sealed interface DownloadEvent {
    data class OnBookClick(val bookId: String) : DownloadEvent
}
