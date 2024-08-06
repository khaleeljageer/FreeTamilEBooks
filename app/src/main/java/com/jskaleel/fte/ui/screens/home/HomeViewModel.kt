package com.jskaleel.fte.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.R
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.domain.model.Book
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
    private val getBooksUseCase: GetBooksUseCase
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState())

    val uiState = viewModelState.map {
        it.toUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = viewModelState.value.toUiState()
    )

    init {
        viewModelState.update { it.copy(loading = true) }
        viewModelScope.launch { }
        viewModelScope.launch(Dispatchers.IO) {
            getBooksUseCase.getBooks()
                .collect { books ->
                    if (books.isNotEmpty()) {
                        viewModelState.update {
                            it.copy(
                                loading = false,
                                books = books
                            )
                        }
                    }
                }
        }
        refreshBooksIfNeeded()
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
            /*EPubReader.getReader().download(item.epub)
                .onSuccess {
                    Timber.tag("Khaleel").d("onSuccess: $it")
                }.onFailure {
                    Timber.tag("Khaleel").d("onFailure: $it")
                }*/
        }
    }
}

private data class HomeViewModelState(
    val loading: Boolean = false,
    val books: List<Book> = emptyList(),
) {
    fun toUiState() = HomeViewModelUiState(
        loading = loading,
        books = books.map {
            BooksUiModel(
                title = it.title,
                author = it.author,
                category = it.category,
                image = it.image,
                icon = ImageType.ResourceImage(R.drawable.ic_download)
            )
        },
    )
}

data class HomeViewModelUiState(
    val loading: Boolean,
    val books: List<BooksUiModel>,
)

sealed class BookUiState {
    object Loading : BookUiState()
    data class Success(val books: List<Book>) : BookUiState()
    data class Error(val message: String) : BookUiState()
}