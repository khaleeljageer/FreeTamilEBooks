package com.jskaleel.fte.ui.screens.main.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class BookShelfViewModel @Inject constructor() : ViewModel() {
    private val viewModelState = MutableStateFlow(BookShelfViewModelState(isLoading = true))

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
        viewModelScope.launch(Dispatchers.IO) {
            viewModelState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun getBooks() {

    }

    private fun refreshBooksIfNeeded() {

    }

    fun downloadBook(index: Int) {

    }

    fun onSearchResultClick(label: String) {

    }

//    private fun List<Book>.getFilteredBooks(lowercaseQuery: String): List<Book> {
//        return this.filter { book ->
//            book.title.lowercase().contains(lowercaseQuery) ||
//                    book.author.lowercase().contains(lowercaseQuery)
//        }
//    }

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
//            val searchResult = searchUseCase.searchByQuery(query)
//            viewModelState.update {
//                it.copy(searchList = searchResult)
//            }
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

private data class BookShelfViewModelState(
    val downloadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val books: List<String> = emptyList(),
    val searchList: Set<String> = emptySet(),
    val error: String? = null,
    val searchQuery: String = "",
    val searchActive: Boolean = false,
) {
    fun toUiState() =
        when {
            isLoading -> BookShelfViewModelUiState.Loading
            else -> BookShelfViewModelUiState.Success(
                books = emptyList(),
                searchQuery = searchQuery,
                searchActive = searchActive,
                searchList = searchList.toList(),
            )
        }
}

sealed class BookShelfViewModelUiState {
    data object Loading : BookShelfViewModelUiState()
    data class Success(
        val books: List<BookUiModel>,
        val searchList: List<String>,
        val searchQuery: String,
        val searchActive: Boolean,
    ) : BookShelfViewModelUiState()
}