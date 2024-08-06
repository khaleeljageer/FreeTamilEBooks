package com.jskaleel.fte.ui.screens.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.core.model.ErrorState
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.usecase.DownloadedBooksUseCase
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
    private val downloadedBooksUseCase: DownloadedBooksUseCase
) : ViewModel() {
    private val viewModelState = MutableStateFlow(DownloadViewModelState(isEmpty = true))

    val uiState = viewModelState.map {
        it.toUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = viewModelState.value.toUiState()
    )

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelState.update { it.copy(isEmpty = true) }
        viewModelScope.launch(Dispatchers.IO) {
            downloadedBooksUseCase.getBooks()
                .collect { books ->
                    if (books.isNotEmpty()) {
                        viewModelState.update {
                            it.copy(
                                books = books.reversed(),
                                isEmpty = books.isEmpty()
                            )
                        }
                    }
                }
        }
    }

    fun itemRemoved(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }
}

data class DownloadViewModelState(
    val isEmpty: Boolean = false,
    val error: String? = null,
    val books: List<Book> = emptyList(),
    val errorState: ErrorState = ErrorState.none,
) {
    fun toUiState() = when {
        isEmpty -> DownloadViewModelUiState.Empty
        error != null -> DownloadViewModelUiState.Error(error)
        else -> DownloadViewModelUiState.Success(
            books = books.map {
                SavedBookUiModel(
                    title = it.title,
                    author = it.author,
                    category = it.category,
                    image = it.image,
                )
            },
            error = errorState
        )
    }
}


sealed class DownloadViewModelUiState {
    data object Empty : DownloadViewModelUiState()
    data class Success(
        val books: List<SavedBookUiModel>,
        val error: ErrorState
    ) : DownloadViewModelUiState()

    data class Error(val message: String) : DownloadViewModelUiState()
}