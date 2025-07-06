package com.jskaleel.fte.ui.screens.main.bookshelf

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.getImagePainter
import com.jskaleel.fte.ui.screens.common.components.FteCard
import com.jskaleel.fte.ui.screens.common.extensions.isScrollingUp
import com.jskaleel.fte.ui.theme.FTEBooksTheme
import com.jskaleel.fte.ui.theme.dimension
import kotlinx.coroutines.launch

@Composable
fun BookListContent(
    searchQuery: String,
    searchActive: Boolean,
    onSearchClear: CallBack,
    onSearchActiveChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onSearchResultClick: (String) -> Unit,
    onDownloadClick: (Int) -> Unit,
    books: List<BookUiModel>,
    searchList: List<String>,
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimension.medium),
            contentPadding = PaddingValues(vertical = MaterialTheme.dimension.medium),
            modifier = Modifier
                .clipToBounds()
                .padding(horizontal = MaterialTheme.dimension.medium)
        ) {
            itemsIndexed(books) { index, book ->
                BookItem(
                    onDownloadClick = { onDownloadClick(index) },
                    image = book.image,
                    title = book.title,
                    author = book.author,
                    category = book.category,
                    downloaded = book.downloaded,
                    progress = book.progress,
                )
            }
        }
        AnimatedVisibility(
            visible = !listState.isScrollingUp(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ScrollUp {
                coroutineScope.launch {
                    listState.scrollToItem(index = 0)
                }
            }
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
    val padding = if (!active) {
        PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp)
    } else PaddingValues(0.dp)

    Box(
        modifier = Modifier
            .padding(padding)
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    expanded = active,
                    onExpandedChange = onActiveChange,
                    placeholder = {
                        Text(text = "Search")
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
                )
            },
            expanded = active,
            onExpandedChange = onActiveChange,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = SearchBarDefaults.dockedShape,
            colors = SearchBarDefaults.colors(),
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = SearchBarDefaults.windowInsets,
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
private fun ScrollUp(onClick: CallBack) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text("Scroll to top")
    }
}

@Composable
fun BookItem(
    onDownloadClick: CallBack,
    title: String,
    author: String,
    category: String,
    image: ImageType,
    downloaded: Boolean,
    progress: Boolean,
) {
    FteCard(
        modifier = Modifier.height(180.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Image(
                painter = image.getImagePainter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(160.dp)
                    .width(120.dp)
                    .aspectRatio(ratio = 0.75f)
                    .clip(
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clipToBounds()
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier.height(160.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                ) {

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = author,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryText(
                            label = category
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        if (!downloaded) {
                            DownloadIcon(
                                onClick = onDownloadClick
                            )
                        }
                    }

                    AnimatedContent(targetState = progress, label = "progress_anim") { state ->
                        if (state) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .height(2.dp)
                                    .fillMaxWidth(),
                                strokeCap = StrokeCap.Round
                            )
                        } else {
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadIcon(
    onClick: CallBack
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Outlined.SaveAlt,
            contentDescription = "Download"
        )
    }
}

@Composable
fun CategoryText(
    label: String,
    modifier: Modifier = Modifier,
) {
    val color = MaterialTheme.colorScheme.primaryContainer
    Box(
        modifier = modifier.then(
            Modifier
                .background(
                    color = color.copy(alpha = 0.6f), CircleShape
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookItemPreview() {
    FTEBooksTheme {
        BookItem(
            onDownloadClick = { },
            title = "Book Item Preview",
            author = "Author",
            category = "Category",
            image = ImageType.EMPTY,
            downloaded = true,
            progress = true,
        )
    }
}

@Immutable
data class BookUiModel(
    val title: String,
    val image: ImageType,
    val author: String,
    val category: String,
    val downloaded: Boolean,
    val progress: Boolean,
)