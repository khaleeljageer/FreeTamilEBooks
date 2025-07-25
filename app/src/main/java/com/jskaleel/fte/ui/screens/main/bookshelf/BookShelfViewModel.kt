package com.jskaleel.fte.ui.screens.main.bookshelf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.core.model.ErrorState
import com.jskaleel.fte.core.model.onError
import com.jskaleel.fte.core.model.onSuccess
import com.jskaleel.fte.core.model.toErrorState
import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.usecase.BookShelfUseCase
import com.jskaleel.fte.ui.utils.mutableNavigationState
import com.jskaleel.fte.ui.utils.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class BookShelfViewModel @Inject constructor(
    private val useCase: BookShelfUseCase
) : ViewModel() {
    private val mutex = Mutex()

    var navigation by mutableNavigationState<BookShelfNavigationState>()
        private set
    private val viewModelState = MutableStateFlow(BookShelfViewModelState(loading = true))

    val uiState = viewModelState.map {
        it.toUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = viewModelState.value.toUiState()
    )

    init {
        syncBooks()
        observeDownloadStatus()
        observeDownloads()
    }

    private fun observeDownloads() {
        viewModelScope.launch {
            useCase.fetchDownloadedBooks().collect { downloads ->
                viewModelState.update {
                    it.copy(downloadedBooks = downloads)
                }
            }
        }
    }

    private fun observeDownloadStatus() {
        viewModelScope.launch {
            useCase.downloadStatus.collect { result ->
                when (result) {
                    is DownloadResult.Queued -> {
                        updateBook(result.id) { it.copy(downloading = true) }
                    }

                    is DownloadResult.Success -> {
                        updateBook(result.id) {
                            it.copy(
                                downloading = false,
                                downloaded = true,
                            )
                        }
                    }

                    is DownloadResult.Error -> updateBook(result.id) {
                        it.copy(downloading = false)
                    }

                    is DownloadResult.Progress -> {
                        updateBook(result.id) {
                            it.copy(
                                downloading = true,
                                downloadProgress = result.percent
                            )
                        }
                    }
                }
            }
        }
    }

    private fun syncBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.syncIfNeeded()
                .onError { code, message ->
                    if (message != null) {
                        viewModelState.update { it.copy(error = message.toErrorState()) }
                    }
                }

            val books = useCase.observeBooks().first()
            viewModelState.update {
                it.copy(
                    loading = false,
                    books = books
                )
            }
        }
    }

    fun onEvent(event: BookListEvent) {
        when (event) {
            is BookListEvent.OnDownloadClick -> {
                val book = viewModelState.value.books.first { it.id == event.bookId }
                if (!book.downloaded && !book.downloading) {
                    updateBook(book.id) {
                        it.copy(
                            downloading = true,
                            downloadProgress = 0
                        )
                    }
                    useCase.startDownload(bookId = book.id, title = book.title, url = book.url)
                }
            }

            is BookListEvent.OnOpenClick -> {
                openBook(event.bookId)
            }

            is BookListEvent.OnDeleteClick -> {
                viewModelScope.launch {
                    useCase.deleteBook(event.bookId)
                }
            }
        }
    }

    private fun updateBook(id: String, transform: (Book) -> Book) {
        viewModelScope.launch {
            viewModelState.update { state ->
                val updatedBooks = state.books.map {
                    if (it.id == id) transform(it) else it
                }
                state.copy(books = updatedBooks)
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
            val readerId = useCase.getReaderId(bookId)
            if (readerId != -1L) {
                useCase.openBook(readerId)
                    .onSuccess {
                        viewModelState.update {
                            it.copy(
                                showLoadingDialog = false
                            )
                        }
                        navigation = navigate(BookShelfNavigationState.OpenBook(readerId))
                    }
                    .onError { code, message ->
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
                        error = "புத்தகம் இல்லை அல்லது பதிவிறக்கப்படவில்லை.".toErrorState(),
                        showLoadingDialog = false
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

private data class BookShelfViewModelState(
    val loading: Boolean = true,
    val showLoadingDialog: Boolean = false,
    val books: List<Book> = emptyList(),
    val error: ErrorState = ErrorState.none,
    val downloadedBooks: List<String> = emptyList(),
) {
    fun toUiState() =
        when {
            loading -> BookShelfUiState.Loading
            else -> BookShelfUiState.Success(
                books = books.map {
                    BookUiModel(
                        id = it.id,
                        title = it.title,
                        author = it.author,
                        category = it.category,
                        image = it.image,
                        downloaded = downloadedBooks.any { it1 ->
                            it1 == it.id
                        },
                        downloading = it.downloading,
                    )
                },
                error = error,
                showLoadingDialog = showLoadingDialog,
            )
        }
}

sealed class BookShelfUiState {
    data object Loading : BookShelfUiState()
    data class Success(
        val books: List<BookUiModel>,
        val error: ErrorState,
        val showLoadingDialog: Boolean,
    ) : BookShelfUiState()
}

sealed interface BookShelfNavigationState {
    data class OpenBook(val readerId: Long) : BookShelfNavigationState
}

sealed interface BookListEvent {
    data class OnDownloadClick(val bookId: String) : BookListEvent
    data class OnOpenClick(val bookId: String) : BookListEvent
    data class OnDeleteClick(val bookId: String) : BookListEvent
}
