package com.jskaleel.fte.ui.screens.main.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.model.CategoryItem
import com.jskaleel.fte.domain.model.RecentReadItem
import com.jskaleel.fte.domain.usecase.SearchUseCase
import com.jskaleel.fte.ui.screens.main.downloads.BookUiModel
import com.jskaleel.fte.ui.screens.main.search.SearchNavigationState.OpenBook
import com.jskaleel.fte.ui.utils.mutableNavigationState
import com.jskaleel.fte.ui.utils.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val useCase: SearchUseCase
) : ViewModel() {
    var navigation by mutableNavigationState<SearchNavigationState>()
        private set

    private val viewModelState = MutableStateFlow(SearchViewModelState())
    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )

    init {
        fetchCategories()
        fetchRecentReads()
    }

    private fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.fetchCategories().collect { categories ->
                viewModelState.update {
                    it.copy(categories = categories)
                }
            }
        }
    }

    private fun fetchRecentReads() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.fetchRecentReads().collect { books ->
                viewModelState.update {
                    it.copy(recentReads = books)
                }
            }
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnBookClick -> {
                navigation = navigate(OpenBook(id = event.bookId))
            }

            is SearchEvent.OnCategoryClick -> {}
            is SearchEvent.OnDownloadClick -> {}
            is SearchEvent.OnSearchQueryChange -> {
                viewModelState.update {
                    it.copy(
                        searchQuery = event.query,
                        searchResult = emptyList(),
                        hasSearched = false
                    )
                }
            }

            is SearchEvent.OnSearchClick -> {
                if (event.query.isNotBlank()) {
                    updateResults(event.query)
                } else {
                    resetToDefault()
                }
            }

            is SearchEvent.OnActiveChange -> {
                viewModelState.update {
                    it.copy(active = event.active)
                }
                if (!event.active) {
                    resetToDefault()
                }
            }
        }
    }

    private fun resetToDefault() {
        viewModelState.update {
            it.copy(
                searchQuery = "",
                searchResult = emptyList(),
                hasSearched = false,
                loading = false
            )
        }
    }

    private fun updateResults(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelState.update {
                it.copy(
                    loading = true,
                    searchQuery = query,
                    hasSearched = true
                )
            }
            useCase.fetchBooksByQuery(query).collect { result ->
                viewModelState.update {
                    it.copy(
                        loading = false,
                        searchResult = result
                    )
                }
            }
        }
    }
}

private data class SearchViewModelState(
    val loading: Boolean = false,
    val searchQuery: String = "",
    val searchResult: List<Book> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val recentReads: List<RecentReadItem> = emptyList(),
    val active: Boolean = false,
    val hasSearched: Boolean = false,
) {
    fun toUiState(): SearchUiState {
        return when {
            loading -> SearchUiState.Loading
            hasSearched -> {
                if (searchResult.isNotEmpty()) {
                    val books = searchResult.map {
                        SearchBookUiModel(
                            title = it.title,
                            author = it.author,
                            image = it.image,
                            id = it.id,
                            category = it.category,
                            downloaded = it.downloaded,
                        )
                    }
                    SearchUiState.Success.SearchResult(
                        books = books,
                        active = active,
                        searchQuery = searchQuery,
                    )
                } else {
                    SearchUiState.Success.EmptySearchResult(
                        active = active,
                        searchQuery = searchQuery,
                    )
                }
            }

            else -> {
                SearchUiState.Success.DefaultResult(
                    categories = categories.map {
                        CategoryUiModel(
                            name = it.name,
                            count = it.count
                        )
                    },
                    recentReads = recentReads.map {
                        RecentUiModel(
                            id = it.id,
                            title = it.title,
                            image = it.image,
                            lastRead = it.lastRead
                        )
                    },
                    active = active,
                    searchQuery = searchQuery,
                )
            }
        }
    }
}

sealed interface SearchUiState {
    data object Loading : SearchUiState

    sealed interface Success {

        data class SearchResult(
            val books: List<SearchBookUiModel>,
            val active: Boolean,
            val searchQuery: String,
        ) : SearchUiState

        data class DefaultResult(
            val categories: List<CategoryUiModel>,
            val recentReads: List<RecentUiModel>,
            val active: Boolean,
            val searchQuery: String,
        ) : SearchUiState

        data class EmptySearchResult(
            val active: Boolean,
            val searchQuery: String,
        ) : SearchUiState
    }
}

sealed interface SearchNavigationState {
    data class OpenBook(val id: String) : SearchNavigationState
}

sealed interface SearchEvent {
    data class OnBookClick(val bookId: String) : SearchEvent
    data class OnDownloadClick(val bookId: String) : SearchEvent
    data class OnCategoryClick(val category: String) : SearchEvent
    data class OnSearchClick(val query: String) : SearchEvent
    data class OnSearchQueryChange(val query: String) : SearchEvent
    data class OnActiveChange(val active: Boolean) : SearchEvent
}
