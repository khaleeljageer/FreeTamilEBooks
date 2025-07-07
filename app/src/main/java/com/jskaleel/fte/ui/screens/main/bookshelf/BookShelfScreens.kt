package com.jskaleel.fte.ui.screens.main.bookshelf

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.ui.screens.common.components.BookItem
import com.jskaleel.fte.ui.screens.common.extensions.isScrollingUp
import com.jskaleel.fte.ui.theme.FTEBooksTheme
import com.jskaleel.fte.ui.theme.dimension
import kotlinx.coroutines.launch

@Composable
fun BookShelfContent(
    event: (BookListEvent) -> Unit,
    books: List<BookUiModel>,
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = MaterialTheme.dimension.small),
            modifier = Modifier.clipToBounds()
        ) {
            items(
                items = books,
                key = { it.id }
            ) { book ->
                Log.d("BookShelfContent", "BookShelfContent: ${book.downloading} - ${book.title}")
                BookItem(
                    onDownloadClick = {
                        event.invoke(BookListEvent.OnDownloadClick(bookId = book.id))
                    },
                    onOpenClick = {
                        event.invoke(BookListEvent.OnOpenClick(bookId = book.id))
                    },
                    image = book.image,
                    title = book.title,
                    author = book.author,
                    category = book.category,
                    downloaded = book.downloaded,
                    downloading = book.downloading,
                )
                HorizontalDivider(thickness = (0.8).dp)
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
                    listState.animateScrollToItem(index = 0)
                }
            }
        }
    }
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
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookItemPreview() {
    FTEBooksTheme {
        Column {

            BookItem(
                onDownloadClick = { },
                onOpenClick = { },
                title = "Book Item Preview",
                author = "Author",
                category = "Category",
                image = ImageType.EMPTY,
                downloaded = true,
                downloading = false,
            )

            BookItem(
                onDownloadClick = { },
                onOpenClick = { },
                title = "Book Item Preview",
                author = "Author",
                category = "Category",
                image = ImageType.EMPTY,
                downloaded = false,
                downloading = true,
            )
        }
    }
}

@Immutable
data class BookUiModel(
    val id: String,
    val title: String,
    val image: ImageType,
    val author: String,
    val category: String,
    val downloaded: Boolean,
    val downloading: Boolean,
)