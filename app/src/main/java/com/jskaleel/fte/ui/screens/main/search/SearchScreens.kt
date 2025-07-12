package com.jskaleel.fte.ui.screens.main.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.R
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.core.StringCallBack
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.ui.screens.common.components.BookItem
import com.jskaleel.fte.ui.screens.common.components.RecentBookItem
import com.jskaleel.fte.ui.theme.FTEBooksTheme
import com.jskaleel.fte.ui.theme.dimension

@Composable
fun DefaultSearchContent(
    onEvent: (SearchEvent) -> Unit,
    categories: List<CategoryUiModel>,
    recentReads: List<RecentUiModel>,
) {
    SearchResultListContent(
        onEvent = onEvent,
        categories = categories,
        recentReads = recentReads
    )
}

@Composable
fun SearchResultContent(
    onEvent: (SearchEvent) -> Unit,
    books: List<SearchBookUiModel>,
) {
    SearchResultListContent(
        onEvent = onEvent,
        books = books,
        categories = emptyList(),
        recentReads = emptyList()
    )
}

@Composable
private fun SearchResultListContent(
    onEvent: (SearchEvent) -> Unit,
    books: List<SearchBookUiModel> = emptyList(),
    categories: List<CategoryUiModel> = emptyList(),
    recentReads: List<RecentUiModel> = emptyList()
) {
    if (books.isNotEmpty()) {
        SearchedBooks(
            onEvent = onEvent,
            books = books
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            if (categories.isNotEmpty()) {
                CategorySection(
                    onCategoryClick = {
                        onEvent(SearchEvent.OnCategoryClick(category = it))
                    },
                    categories = categories
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (recentReads.isNotEmpty()) {
                RecentReadSection(
                    recentReads = recentReads,
                )
            }
        }
    }
}

@Composable
private fun SearchedBooks(
    onEvent: (SearchEvent) -> Unit,
    books: List<SearchBookUiModel>
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = MaterialTheme.dimension.small),
        modifier = Modifier.clipToBounds()
    ) {
        items(
            items = books,
            key = { it.id }
        ) { book ->
            BookItem(
                onOpenClick = {
                    onEvent(SearchEvent.OnBookClick(bookId = book.id))
                },
                title = book.title,
                author = book.author,
                category = book.category,
                image = book.image,
                onDownloadClick = {
                    onEvent(SearchEvent.OnDownloadClick(bookId = book.id))
                },
                downloaded = book.downloaded,
                downloading = book.downloading,
            )
            HorizontalDivider(thickness = (0.8).dp)
        }
    }
}

@Composable
private fun RecentReadSection(recentReads: List<RecentUiModel>) {
    Text(
        text = "சமீபத்தில் படித்தவை",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalRecentGrid(
        books = recentReads,
        onItemClick = { }
    )
}

@Composable
private fun CategorySection(
    onCategoryClick: StringCallBack,
    categories: List<CategoryUiModel>,
) {
    Text(
        text = "நூல் வகைகள்",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalCategoryGrid(
        categories = categories,
        onCategoryClick = {
            onCategoryClick(it.name)
        }
    )
}

@Composable
private fun HorizontalRecentGrid(
    modifier: Modifier = Modifier,
    books: List<RecentUiModel>,
    onItemClick: () -> Unit,
    rows: Int = 3
) {
    val itemsPerColumn = (books.size + rows - 1) / rows
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(itemsPerColumn) { columnIndex ->
            Column(
                modifier = Modifier.widthIn(min = 180.dp, max = 220.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(rows) { rowIndex ->
                    val itemIndex = columnIndex * rows + rowIndex
                    if (itemIndex < books.size) {
                        RecentBookItem(
                            onClick = { onItemClick() },
                            title = books[itemIndex].title,
                            image = books[itemIndex].image,
                            lastRead = books[itemIndex].lastRead
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HorizontalCategoryGrid(
    categories: List<CategoryUiModel>,
    modifier: Modifier = Modifier,
    rows: Int = 5,
    onCategoryClick: (CategoryUiModel) -> Unit
) {
    val itemsPerColumn = (categories.size + rows - 1) / rows

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(itemsPerColumn) { columnIndex ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(rows) { rowIndex ->
                    val itemIndex = columnIndex * rows + rowIndex
                    if (itemIndex < categories.size) {
                        CategoryChip(
                            category = categories[itemIndex],
                            onClick = { onCategoryClick(categories[itemIndex]) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: CategoryUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Transparent)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = "${category.name} (${category.count})",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ColumnScope.EmptySearchResultContent(searchQuery: String) {
    if (searchQuery.isNotEmpty()) {
        val text = "\"$searchQuery\"-க்கு எந்த புத்தகங்களும் கிடைக்கவில்லை"
        val annotatedString = buildAnnotatedString {
            var startIndex = 0
            val lowercase = text.lowercase()
            val queryLowercase = searchQuery.lowercase()

            while (startIndex < text.length) {
                val index = lowercase.indexOf(string = queryLowercase, startIndex)
                if (index == -1) {
                    append(text.substring(startIndex))
                    break
                }
                append(text.substring(startIndex, index))
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
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SearchTopBar(
    onActiveChange: (Boolean) -> Unit,
    onQueryChange: StringCallBack,
    onClearClick: CallBack,
    onKeyboardSearchClick: StringCallBack,
    query: String,
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
            modifier = Modifier
                .weight(weight = 1f)
                .onFocusChanged {
                    if (it.isFocused) onActiveChange(true)
                },
            placeholder = {
                Text(
                    text = stringResource(R.string.search_books),
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
                unfocusedBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onBackground
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onKeyboardSearchClick(query)
                }
            )
        )
    }
}

@Immutable
data class CategoryUiModel(
    val name: String,
    val count: Int
)

@Immutable
data class RecentUiModel(
    val title: String,
    val id: String,
    val image: ImageType,
    val lastRead: String
)

@Immutable
data class SearchBookUiModel(
    val title: String,
    val id: String,
    val author: String,
    val category: String,
    val image: ImageType,
    val downloaded: Boolean,
    val downloading: Boolean = false,
)

@Preview(showBackground = true)
@Composable
private fun SearchTopBarPreview() {
    FTEBooksTheme {
        SearchTopBar(
            query = "Search Query",
            onQueryChange = {},
            onClearClick = {},
            onActiveChange = {},
            onKeyboardSearchClick = {}
        )
    }
}