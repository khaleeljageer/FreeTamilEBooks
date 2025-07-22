package com.jskaleel.fte.ui.screens.main.downloads

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.core.model.ErrorState
import com.jskaleel.fte.core.model.onError
import com.jskaleel.fte.core.model.onSuccess
import com.jskaleel.fte.core.model.toErrorState
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.usecase.DownloadsUseCase
import com.jskaleel.fte.ui.utils.mutableNavigationState
import com.jskaleel.fte.ui.utils.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val useCase: DownloadsUseCase,
) : ViewModel() {
    private val mutex = Mutex()

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
                openBook(event.bookId)
            }

            is DownloadEvent.OnDeleteClick -> {
                viewModelScope.launch {
                    useCase.deleteBook(event.bookId)
                }
            }
        }
    }

    private fun openBook(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelState.update {
                it.copy(
                    showLoadingDialog = true
                )
            }
            val readerId = viewModelState.value.books.firstOrNull { it.id == bookId }?.readerId
            if (readerId != null) {
                useCase.openBook(readerId)
                    .onSuccess {
                        useCase.updateLastRead(readerId)
                        viewModelState.update {
                            it.copy(
                                showLoadingDialog = false
                            )
                        }
                        navigation = navigate(
                            DownloadNavigationState.OpenBook(readerId)
                        )
                    }
                    .onError { _, message ->
                        viewModelState.update {
                            it.copy(
                                error = message?.toErrorState() ?: ErrorState.none,
                                showLoadingDialog = false
                            )
                        }
                    }
            } else {
                viewModelState.update {
                    it.copy(
                        error = "புத்தகத்தை திறக்கமுடியவில்லை...".toErrorState(),
                        showLoadingDialog = false
                    )
                }
            }
        }
    }

    init {
        observerDownloadedBooks()
    }

    private fun observerDownloadedBooks() {
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

    private suspend inline fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
        mutex.withLock {
            yield()
            this.value = function(this.value)
        }
    }
}

private data class DownloadViewModelState(
    val loading: Boolean = true,
    val error: ErrorState = ErrorState.none,
    val books: List<Book> = emptyList(),
    val showLoadingDialog: Boolean = false,
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
                    },
                    error = error,
                    showLoadingDialog = showLoadingDialog
                )
            }
        }
    }
}

sealed interface DownloadUiState {
    data object Loading : DownloadUiState
    data object Empty : DownloadUiState
    data class Success(
        val books: List<BookUiModel>,
        val error: ErrorState,
        val showLoadingDialog: Boolean,
    ) : DownloadUiState
}

sealed interface DownloadNavigationState {
    data class OpenBook(val readerId: Long) : DownloadNavigationState
}

sealed interface DownloadEvent {
    data class OnBookClick(val bookId: String) : DownloadEvent
    data class OnDeleteClick(val bookId: String) : DownloadEvent
}
