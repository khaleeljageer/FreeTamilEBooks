package com.jskaleel.fte.ui.screens.main.search

import androidx.compose.runtime.Stable
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
import com.jskaleel.fte.domain.model.CategoryItem
import com.jskaleel.fte.domain.model.RecentReadItem
import com.jskaleel.fte.domain.usecase.SearchUseCase
import com.jskaleel.fte.ui.screens.main.bookshelf.BookShelfNavigationState
import com.jskaleel.fte.ui.utils.mutableNavigationState
import com.jskaleel.fte.ui.utils.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val useCase: SearchUseCase
) : ViewModel() {
    private val mutex = Mutex()

    var navigation by mutableNavigationState<SearchNavigationState>()
        private set

    private val viewModelState = MutableStateFlow(SearchViewModelState())
    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )

    val searchBarState = viewModelState
        .map { state ->
            SearchBarState(
                query = state.searchQuery,
                isActive = state.active
            )
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SearchBarState()
        )

    init {
        fetchCategories()
        fetchRecentReads()
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

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnBookClick -> {
                openBook(event.bookId)
            }

            is SearchEvent.OnCategoryClick -> {
                fetchBooksByCategory(event.category)
            }

            is SearchEvent.OnDownloadClick -> {
                val book = viewModelState.value.searchResult.first { it.id == event.bookId }
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

            is SearchEvent.OnSearchQueryChange -> {
                viewModelScope.launch {
                    viewModelState.update { state ->
                        state.copy(
                            searchQuery = event.query,
                            searchResult = if (event.query.isEmpty()) emptyList() else state.searchResult,
                            hasSearched = if (event.query.isEmpty()) false else state.hasSearched
                        )
                    }
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
                viewModelScope.launch {
                    viewModelState.update { it.copy(active = event.active) }
                }
                if (!event.active) {
                    resetToDefault()
                }
            }

            is SearchEvent.OnRecentReadClick -> {
                openBook(event.bookId)
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
                        navigation = navigate(SearchNavigationState.OpenBook(readerId))
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

    private fun fetchBooksByCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.fetchBooksByCategory(category).collect { result ->
                viewModelState.update {
                    it.copy(
                        loading = false,
                        searchResult = result,
                        searchQuery = category,
                        hasSearched = true
                    )
                }
            }
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

    private fun resetToDefault() {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    searchQuery = "",
                    searchResult = emptyList(),
                    hasSearched = false,
                    loading = false
                )
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.fetchCategories().collect { categories ->
                viewModelState.update { it.copy(categories = categories) }
            }
        }
    }

    private fun fetchRecentReads() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.fetchRecentReads().collect { books ->
                viewModelState.update { it.copy(recentReads = books) }
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
                                downloaded = true
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

    private fun updateBook(id: String, transform: (Book) -> Book) {
        viewModelScope.launch {
            viewModelState.update { state ->
                val updatedBooks = state.searchResult.map {
                    if (it.id == id) transform(it) else it
                }
                state.copy(searchResult = updatedBooks)
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

@Stable
data class SearchBarState(
    val query: String = "",
    val isActive: Boolean = false
)

private data class SearchViewModelState(
    val loading: Boolean = false,
    val searchQuery: String = "",
    val searchResult: List<Book> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val recentReads: List<RecentReadItem> = emptyList(),
    val downloadedBooks: List<String> = emptyList(),
    val active: Boolean = false,
    val hasSearched: Boolean = false,
    val showLoadingDialog: Boolean = false,
    val error: ErrorState = ErrorState.none,
) {
    fun toUiState(): SearchUiState {
        return when {
            loading -> SearchUiState.Loading(
                active = false,
                searchQuery = ""
            )

            hasSearched -> {
                val contentType = if (searchResult.isNotEmpty()) {
                    ContentType.SearchResults
                } else {
                    ContentType.EmptyResults
                }

                SearchUiState.Content(
                    contentType = contentType,
                    books = searchResult.map {
                        SearchBookUiModel(
                            title = it.title,
                            author = it.author,
                            image = it.image,
                            id = it.id,
                            category = it.category,
                            downloaded = downloadedBooks.any { it1 ->
                                it1 == it.id
                            },
                            downloading = it.downloading,
                        )
                    },
                    categories = emptyList(),
                    recentReads = emptyList(),
                    active = active,
                    searchQuery = searchQuery,
                    showLoadingDialog = showLoadingDialog,
                    error = error
                )
            }

            else -> {
                SearchUiState.Content(
                    contentType = ContentType.Default,
                    books = emptyList(),
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
                    searchQuery = "",
                    showLoadingDialog = showLoadingDialog,
                    error = error
                )
            }
        }
    }
}

sealed interface SearchUiState {
    val active: Boolean
    val searchQuery: String

    data class Loading(
        override val active: Boolean,
        override val searchQuery: String
    ) : SearchUiState

    data class Content(
        val contentType: ContentType,
        val books: List<SearchBookUiModel> = emptyList(),
        val categories: List<CategoryUiModel> = emptyList(),
        val recentReads: List<RecentUiModel> = emptyList(),
        val showLoadingDialog: Boolean = false,
        val error: ErrorState,
        override val active: Boolean,
        override val searchQuery: String
    ) : SearchUiState
}

@Stable
sealed class ContentType {
    object Default : ContentType()
    object SearchResults : ContentType()
    object EmptyResults : ContentType()
}

sealed interface SearchNavigationState {
    data class OpenBook(val readerId: Long) : SearchNavigationState
}

sealed interface SearchEvent {
    data class OnBookClick(val bookId: String) : SearchEvent
    data class OnDownloadClick(val bookId: String) : SearchEvent
    data class OnCategoryClick(val category: String) : SearchEvent
    data class OnSearchClick(val query: String) : SearchEvent
    data class OnSearchQueryChange(val query: String) : SearchEvent
    data class OnActiveChange(val active: Boolean) : SearchEvent
    data class OnRecentReadClick(val bookId: String) : SearchEvent
}
