package com.jskaleel.fte.ui.screens.main.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jskaleel.fte.R
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.ui.screens.main.downloads.BookUiModel
import com.jskaleel.fte.ui.theme.FTEBooksTheme

@Composable
fun SearchContent(
    onEvent: (SearchEvent) -> Unit,
    books: List<BookUiModel>,
    categories: List<String>,
    recentSearches: List<String>
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        topBar = {
            SearchBarContent(
                query = searchQuery,
                active = isSearchActive,
                onClear = {
                    searchQuery = ""
                    isSearchActive = false
                    keyboardController?.hide()
                },
                onActiveChange = { isActive ->
                    isSearchActive = isActive
                    if (!isActive) {
                        searchQuery = ""
                        keyboardController?.hide()
                    }
                },
                onQueryChange = { query ->
                    searchQuery = query
                },
                onSearch = { query ->
                    onEvent(SearchEvent.OnSearch(query))
                },
                onSearchResultClick = { label ->
                    onEvent(SearchEvent.OnSearchResultClick(label))
                },
                searchList = if (searchQuery.isNotBlank()) {
                    books.map { it.title }.filter { it.contains(searchQuery, ignoreCase = true) }
                } else recentSearches
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding)
        ) {
            Text("Hello", fontSize = 72.sp)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarContent(
    query: String,
    active: Boolean,
    onClear: CallBack,
    onActiveChange: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSearchResultClick: (String) -> Unit,
    searchList: List<String>,
) {
    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = active,
                onExpandedChange = onActiveChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_hint),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    IconButton(
                        onClick = {},
                        enabled = false
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null
                        )
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = null
                            )
                        }
                    }
                },
                colors = SearchBarDefaults.inputFieldColors(
                    cursorColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
        expanded = active,
        onExpandedChange = onActiveChange,
        shape = SearchBarDefaults.dockedShape,
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
            dividerColor = MaterialTheme.colorScheme.onBackground,
        ),
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = if (active) {
            SearchBarDefaults.ShadowElevation
        } else {
            2.dp
        },
        windowInsets = SearchBarDefaults.windowInsets
            .exclude(WindowInsets.statusBars),
        content = {
            if (query.isNotBlank()) {
                LazyColumn {
                    items(searchList) { label ->
                        HighlightedText(
                            onClick = { onSearchResultClick(label) },
                            text = label,
                            searchQuery = query,
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
    )
}


@Composable
private fun HighlightedText(
    onClick: CallBack,
    text: String,
    searchQuery: String,
    style: TextStyle = LocalTextStyle.current
) {
    val annotatedString = buildAnnotatedString {
        var startIndex = 0
        val lowercase = text.lowercase()
        val queryLowercase = searchQuery.lowercase()

        while (startIndex < text.length) {
            val index = lowercase.indexOf(queryLowercase, startIndex)
            if (index == -1) {
                // No more matches, append the rest of the text
                append(text.substring(startIndex))
                break
            }

            // Append the text before the match
            append(text.substring(startIndex, index))

            // Bold the matched part
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(text.substring(index, index + searchQuery.length))
            }

            startIndex = index + searchQuery.length
        }
    }

    Text(
        text = annotatedString,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        style = style
    )
}

@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(shape = RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(12.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(weight = 1f),
            placeholder = {
                Text(
                    text = "புத்தகங்களைத் தேடுங்கள்...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClearClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SearchContentPreview() {
    FTEBooksTheme {
        SearchContent(
            onEvent = {},
            books = emptyList(),
            categories = emptyList(),
            recentSearches = emptyList()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchTopBarPreview() {
    FTEBooksTheme {
        SearchTopBar(
            query = "Search Query",
            onQueryChange = {},
            onClearClick = {}
        )
    }
}