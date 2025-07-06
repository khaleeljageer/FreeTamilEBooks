package com.jskaleel.fte.ui.screens.main.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.core.model.onError
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

    fun downloadBook(index: Int) {

    }
}

private data class BookShelfViewModelState(
    val downloadingItems: Set<String> = emptySet(),
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
                        progress = it.downloadProgress,
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