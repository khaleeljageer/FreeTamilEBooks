package com.jskaleel.fte.ui.screens.main.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jskaleel.fte.ui.screens.common.FullScreenLoader
import kotlinx.coroutines.delay

@Composable
fun SearchScreenRoute(
    viewModel: SearchViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topBarState by viewModel.searchBarState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SearchBarContent(
                topBarState = topBarState,
                onEvent = viewModel::onEvent
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            SearchContentArea(
                uiState = uiState,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
private fun ColumnScope.SearchContentArea(
    uiState: SearchUiState,
    onEvent: (SearchEvent) -> Unit
) {
    when (uiState) {
        is SearchUiState.Loading -> {
            FullScreenLoader()
        }

        is SearchUiState.Content -> {
            val resultType = remember(uiState) {
                when (uiState.contentType) {
                    ContentType.Default -> "default"
                    ContentType.EmptyResults -> "empty"
                    ContentType.SearchResults -> "result"
                }
            }

            AnimatedContent(
                targetState = resultType,
                label = "SearchContentAnimation"
            ) { contentType ->
                when (contentType) {
                    "default" -> DefaultSearchContent(
                        onEvent = onEvent,
                        categories = uiState.categories,
                        recentReads = uiState.recentReads,
                    )

                    "result" -> SearchResultContent(
                        onEvent = onEvent,
                        books = uiState.books,
                    )

                    "empty" -> EmptySearchResultContent(
                        searchQuery = uiState.searchQuery,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBarContent(
    onEvent: (SearchEvent) -> Unit,
    topBarState: SearchBarState
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value
    val shouldClearFocus = !topBarState.isActive && isFocused

    SearchTopBar(
        query = topBarState.query,
        onQueryChange = { query ->
            onEvent(SearchEvent.OnSearchQueryChange(query))
        },
        onActiveChange = {
            onEvent(SearchEvent.OnActiveChange(it))
            if (!it) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        },
        onClearClick = {
            onEvent(SearchEvent.OnSearchQueryChange(""))
            focusManager.clearFocus()
            keyboardController?.hide()
            onEvent(SearchEvent.OnActiveChange(false))
        },
        onKeyboardSearchClick = {
            onEvent(SearchEvent.OnActiveChange(false))
            focusManager.clearFocus()
            keyboardController?.hide()
            onEvent(SearchEvent.OnSearchClick(query = it))
        }
    )

    LaunchedEffect(topBarState.isActive) {
        if (shouldClearFocus) {
            delay(100)
            focusManager.clearFocus()
        }
    }
}
