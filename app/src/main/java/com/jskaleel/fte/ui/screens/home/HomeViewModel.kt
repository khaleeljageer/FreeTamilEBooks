package com.jskaleel.fte.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.core.model.DownloadResult
import com.jskaleel.fte.core.model.ErrorState
import com.jskaleel.fte.core.model.toLocalErrorState
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.usecase.DownloadUseCase
import com.jskaleel.fte.domain.usecase.GetBooksUseCase
import com.jskaleel.fte.domain.usecase.RefreshBooksUseCase
import com.jskaleel.fte.domain.usecase.SearchUseCase
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
class HomeViewModel @Inject constructor(
    private val refreshBooksUseCase: RefreshBooksUseCase,
    private val getBooksUseCase: GetBooksUseCase,
    private val downloadUseCase: DownloadUseCase,
    private val searchUseCase: SearchUseCase,
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
        getBooks()
    }

    private fun getBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            getBooksUseCase.getBooks()
                .collect { books ->
                    val searchQuery = viewModelState.value.searchQuery.lowercase()
                    if (searchQuery.isNotBlank()) {
                        viewModelState.update {
                            it.copy(
                                books = books.getFilteredBooks(searchQuery)
                            )
                        }
                    } else {
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
    }

    private fun refreshBooksIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            refreshBooksUseCase.refreshBooks()
        }
    }

    fun downloadBook(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = viewModelState.value.books[index]
            downloadUseCase.downloadBook(item.bookid, item.epub, item.title)
                .collect { result ->
                    when (result) {
                        is DownloadResult.Error -> {
                            viewModelState.update {
                                it.copy(
                                    errorState = (result.exception.message
                                        ?: "").toLocalErrorState()
                                )
                            }
                        }

                        is DownloadResult.Progress -> {
                            val items = viewModelState.value.downloadingItems.toMutableSet()
                            items.add(result.id)
                            viewModelState.update {
                                it.copy(downloadingItems = items)
                            }
                        }

                        is DownloadResult.Queued -> {
                            val items = viewModelState.value.downloadingItems.toMutableSet()
                            items.add(result.id)
                            viewModelState.update {
                                it.copy(downloadingItems = items)
                            }
                        }

                        is DownloadResult.Success -> {
                            val items = viewModelState.value.downloadingItems.toMutableSet()
                            items.remove(result.id)
                            viewModelState.update {
                                it.copy(downloadingItems = items)
                            }
                        }
                    }
                }
        }
    }

    fun onSearchResultClick(label: String) {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelState.update {
                it.copy(
                    searchQuery = label,
                    searchActive = false,
                )
            }
            val lowercaseQuery = label.lowercase()
            val books = viewModelState.value.books
            val searchedBooks = books.getFilteredBooks(lowercaseQuery)
            viewModelState.update {
                it.copy(
                    books = searchedBooks
                )
            }
        }
    }

    private fun List<Book>.getFilteredBooks(lowercaseQuery: String): List<Book> {
        return this.filter { book ->
            book.title.lowercase().contains(lowercaseQuery) ||
                    book.author.lowercase().contains(lowercaseQuery)
        }
    }

    fun onSearchActiveChange(active: Boolean) {
        viewModelState.update { state ->
            state.copy(
                searchActive = active,
                searchList = if (active) state.searchList else emptySet()
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        viewModelState.update {
            it.copy(
                searchQuery = query
            )
        }
        if (query.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                searchItem(query = query)
            }
        }
    }

    /*
    * The callback to be invoked when the input service triggers the ImeAction.Search action.
    * The current query comes as a parameter of the callback.
    * */
    fun onSearchClick(query: String) {
        onSearchResultClick(query)
    }

    private suspend fun searchItem(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val searchResult = searchUseCase.searchByQuery(query)
            viewModelState.update {
                it.copy(searchList = searchResult)
            }
        }
    }

    fun onSearchClear() {
        viewModelState.update {
            it.copy(
                searchQuery = "",
                searchList = emptySet(),
            )
        }
        loadBooks()
    }
}

private data class HomeViewModelState(
    val downloadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val searchList: Set<String> = emptySet(),
    val error: String? = null,
    val errorState: ErrorState = ErrorState.none,
    val searchQuery: String = "",
    val searchActive: Boolean = false,
) {
    fun toUiState() =
        when {
            isLoading -> HomeViewModelUiState.Loading
            error != null -> HomeViewModelUiState.Error(error)
            else -> HomeViewModelUiState.Success(
                books = books.map {
                    BookUiModel(
                        title = it.title,
                        author = it.author,
                        category = it.category,
                        image = it.image,
                        downloaded = it.downloaded,
                        progress = downloadingItems.contains(it.bookid)
                    )
                },
                error = errorState,
                searchQuery = searchQuery,
                searchActive = searchActive,
                searchList = searchList.toList(),
            )
        }
}

sealed class HomeViewModelUiState {
    data object Loading : HomeViewModelUiState()
    data class Success(
        val books: List<BookUiModel>,
        val searchList: List<String>,
        val error: ErrorState,
        val searchQuery: String,
        val searchActive: Boolean,
    ) : HomeViewModelUiState()

    data class Error(val message: String) : HomeViewModelUiState()
}