package com.jskaleel.fte.ui.screens.main.bookshelf

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.core.model.ErrorState
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.consume
import com.jskaleel.fte.ui.screens.common.components.AnimatedLoadingDialog
import com.jskaleel.fte.ui.screens.common.components.BookItem
import com.jskaleel.fte.ui.screens.common.extensions.isScrollingUp
import com.jskaleel.fte.ui.theme.dimension
import com.jskaleel.fte.ui.utils.SnackBarController
import kotlinx.coroutines.launch

@Composable
fun BookShelfContent(
    event: (BookListEvent) -> Unit,
    books: List<BookUiModel>,
    error: ErrorState,
    showLoadingDialog: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackBar = SnackBarController.current

    error.consume {
        snackBar.showMessage(
            message = it.message
        )
    }

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
                BookItem(
                    onOpenClick = {
                        event(BookListEvent.OnOpenClick(bookId = book.id))
                    },
                    title = book.title,
                    author = book.author,
                    category = book.category,
                    image = book.image,
                    onDownloadClick = {
                        event(BookListEvent.OnDownloadClick(bookId = book.id))
                    },
                    onDeleteClick = {
                        event(BookListEvent.OnDeleteClick(bookId = book.id))
                    },
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

    AnimatedLoadingDialog(
        isLoading = showLoadingDialog
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

@Stable
data class BookUiModel(
    val id: String,
    val title: String,
    val image: ImageType,
    val author: String,
    val category: String,
    val downloaded: Boolean,
    val downloading: Boolean,
)
