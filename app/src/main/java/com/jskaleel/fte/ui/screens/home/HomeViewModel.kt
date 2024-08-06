package com.jskaleel.fte.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.usecase.DownloadUseCase
import com.jskaleel.fte.domain.usecase.GetBooksUseCase
import com.jskaleel.fte.domain.usecase.RefreshBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val refreshBooksUseCase: RefreshBooksUseCase,
    private val getBooksUseCase: GetBooksUseCase,
    private val downloadUseCase: DownloadUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    val uiState = viewModelState.map {
        it.toUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = viewModelState.value.toUiState()
    )

    init {
        loadBooks()
        refreshBooksIfNeeded()
    }

    private fun loadBooks() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            getBooksUseCase.getBooks()
                .collect { books ->
                    if (books.isNotEmpty()) {
                        viewModelState.update {
                            it.copy(
                                isLoading = false,
                                books = books
                            )
                        }
                    }
                }
        }
    }

    private fun refreshBooksIfNeeded() {
        viewModelScope.launch {
            refreshBooksUseCase.refreshBooks()
        }
    }

    fun downloadBook(index: Int) {
        viewModelScope.launch {
            val item = viewModelState.value.books[index]
            Timber.tag("Khaleel").d("item: ${item.epub}")
            downloadUseCase.downloadBook(item.bookid, item.epub, item.title)
        }
    }
}

private data class HomeViewModelState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val error: String? = null
) {
    fun toUiState() =
        when {
            isLoading -> HomeViewModelUiState.Loading
            error != null -> HomeViewModelUiState.Error(error)
            else -> HomeViewModelUiState.Success(books = books.map {
                BookUiModel(
                    title = it.title,
                    author = it.author,
                    category = it.category,
                    image = it.image
                )
            })
        }
}

sealed class HomeViewModelUiState {
    data object Loading : HomeViewModelUiState()
    data class Success(val books: List<BookUiModel>) : HomeViewModelUiState()
    data class Error(val message: String) : HomeViewModelUiState()
}