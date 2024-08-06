package com.jskaleel.fte.ui.screens.downloads

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.core.model.ErrorState
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.consume
import com.jskaleel.fte.core.model.getImagePainter
import com.jskaleel.fte.core.utils.CallBack
import com.jskaleel.fte.ui.components.FteCard
import com.jskaleel.fte.ui.screens.home.BookListLoaderContent
import com.jskaleel.fte.ui.screens.home.CategoryText
import com.jskaleel.fte.ui.theme.dimension
import kotlinx.coroutines.launch

@Composable
fun DownloadScreen(
    uiState: DownloadViewModelUiState,
    addBook: CallBack
) {
    when (uiState) {
        is DownloadViewModelUiState.Error -> {

        }

        is DownloadViewModelUiState.Success -> {
            BookListContent(
                books = uiState.books,
                errorState = uiState.error
            )
        }

        DownloadViewModelUiState.Empty -> EmptyContent(
            addBook = addBook
        )
    }
}

@Composable
fun EmptyContent(
    addBook: CallBack
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No books found")
        Button(onClick = addBook) {
            Text("Add books")
        }
    }
}

@Composable
private fun BookListContent(
    books: List<SavedBookUiModel>,
    errorState: ErrorState
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val snackBarHostState = remember { SnackbarHostState() }

    errorState.consume { error ->
        coroutineScope.launch {
            snackBarHostState.showSnackbar(message = error.message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimension.medium),
                contentPadding = PaddingValues(vertical = MaterialTheme.dimension.medium),
                modifier = Modifier
                    .clipToBounds()
                    .padding(horizontal = MaterialTheme.dimension.medium)
            ) {
                itemsIndexed(books) { index, book ->
                    DownloadedBookItem(
                        image = book.image,
                        title = book.title,
                        author = book.author,
                        category = book.category,
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadedBookItem(
    title: String,
    author: String,
    category: String,
    image: ImageType,
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
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = author,
                        style = MaterialTheme.typography.bodyMedium,
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
                    }
                }
            }
        }
    }
}

@Immutable
data class SavedBookUiModel(
    val title: String,
    val category: String,
    val author: String,
    val image: ImageType,
)