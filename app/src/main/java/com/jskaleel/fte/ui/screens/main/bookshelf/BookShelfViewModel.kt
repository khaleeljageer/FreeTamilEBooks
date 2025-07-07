package com.jskaleel.fte.ui.screens.main.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.core.model.onError
import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.usecase.BookShelfUseCase
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
class BookShelfViewModel @Inject constructor(
    private val useCase: BookShelfUseCase
) : ViewModel() {
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
        observeBooks()
        observeDownloadStatus()
    }

    private fun observeDownloadStatus() {
        viewModelScope.launch {
            useCase.downloadStatus.collect { result ->
                when (result) {
                    is DownloadResult.Queued -> {
                        updateBook(result.id) { it.copy(downloading = true) }
                    }

                    is DownloadResult.Success -> updateBook(result.id) {
                        Log.d("BooksViewModel", "Success: $result")
                        it.copy(
                            downloading = false,
                            downloaded = true,
                        )
                    }

                    is DownloadResult.Error -> updateBook(result.id) {
                        Log.d("BooksViewModel", "Error: $result")
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

    private fun observeBooks() {
        viewModelState.update { it.copy(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            useCase.observeBooks().collect { books ->
                viewModelState.update { current ->
                    current.copy(
                        loading = false,
                        books = books
                    )
                }
            }
        }
    }

    private fun syncBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.syncIfNeeded().onError { code, message ->
                Log.d("BooksViewModel", "syncBooks: $code, $message")
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

            }
        }
    }

    private fun updateBook(id: String, transform: (Book) -> Book) {
        viewModelState.update { state ->
            val updatedBooks = state.books.map {
                if (it.id == id) transform(it) else it
            }
            state.copy(books = updatedBooks)
        }
    }
}

private data class BookShelfViewModelState(
    val loading: Boolean = true,
    val books: List<Book> = emptyList()
) {
    fun toUiState() =
        when {
            loading -> BookShelfViewModelUiState.Loading
            else -> BookShelfViewModelUiState.Success(
                books = books.map {
                    BookUiModel(
                        id = it.id,
                        title = it.title,
                        author = it.author,
                        category = it.category,
                        image = it.image,
                        downloaded = it.downloaded,
                        downloading = it.downloading,
                    )
                }
            )
        }
}

sealed class BookShelfViewModelUiState {
    data object Loading : BookShelfViewModelUiState()
    data class Success(
        val books: List<BookUiModel>
    ) : BookShelfViewModelUiState()
}

sealed interface BookListEvent {
    data class OnDownloadClick(val bookId: String) : BookListEvent
    data class OnOpenClick(val bookId: String) : BookListEvent
}