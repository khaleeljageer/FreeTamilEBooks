package com.jskaleel.fte.ui.screens.main.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.ui.screens.main.downloads.BookUiModel
import com.jskaleel.fte.ui.utils.mutableNavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    var navigation by mutableNavigationState<SearchNavigationState>()
        private set

    private val viewModelState = MutableStateFlow(SearchViewModelState())
    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )
}

private data class SearchViewModelState(
    val loading: Boolean = true,
    val books: List<Book> = emptyList(),
    val categories: List<String> = emptyList(),
    val recentSearches: List<String> = emptyList(),
) {
    fun toUiState(): SearchUiState {
        return if (loading) {
            SearchUiState.Loading
        } else {
            if (books.isEmpty()) {
                SearchUiState.Empty
            } else {
                SearchUiState.Success(
                    books = emptyList(),
                    categories = emptyList(),
                    recentSearches = emptyList()
                )
            }
        }
    }
}

sealed interface SearchUiState {
    data object Loading : SearchUiState
    data object Empty : SearchUiState
    data class Success(
        val books: List<BookUiModel>,
        val categories: List<String>,
        val recentSearches: List<String>
    ) : SearchUiState
}

sealed interface SearchNavigationState {
    data class OpenBook(val id: String) : SearchNavigationState
}

sealed interface SearchEvent {
    data class OnBookClick(val bookId: String) : SearchEvent
}
