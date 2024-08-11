package com.jskaleel.fte.ui.screens.downloads

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jskaleel.fte.R
import com.jskaleel.fte.core.model.ErrorState
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.consume
import com.jskaleel.fte.core.model.getImagePainter
import com.jskaleel.fte.core.utils.CallBack
import com.jskaleel.fte.ui.components.FteCard
import com.jskaleel.fte.ui.screens.home.CategoryText
import com.jskaleel.fte.ui.theme.dimension
import kotlinx.coroutines.launch

@Composable
fun DownloadScreen(
    uiState: DownloadViewModelUiState,
    addBook: CallBack,
    onRemove: (String) -> Unit,
    onBookClick: (String) -> Unit
) {
    when (uiState) {
        is DownloadViewModelUiState.Error -> {

        }

        is DownloadViewModelUiState.Success -> {
            BookListContent(
                onRemove = onRemove,
                onBookClick = onBookClick,
                books = uiState.books,
                errorState = uiState.error,
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
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_books))
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LottieAnimation(
            modifier = Modifier.height(280.dp),
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        Button(onClick = addBook) {
            Text("புத்தகங்களை பதிவிறக்கவும்")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookListContent(
    books: List<SavedBookUiModel>,
    errorState: ErrorState,
    onRemove: (String) -> Unit,
    onBookClick: (String) -> Unit,
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
                    .animateContentSize()
            ) {
                items(books) { book ->
                    DownloadedItem(
                        modifier = Modifier.animateItemPlacement(),
                        title = book.title,
                        author = book.author,
                        category = book.category,
                        image = book.image,
                        onRemove = { onRemove(book.id) },
                        onItemClick = { onBookClick(book.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadedItem(
    title: String,
    author: String,
    category: String,
    image: ImageType,
    onRemove: CallBack,
    onItemClick: CallBack,
    modifier: Modifier,
) {
    FteCard(
        modifier = modifier.then(
            Modifier
                .height(180.dp)
                .clickable {
                    onItemClick()
                }
        ),
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
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Immutable
data class SavedBookUiModel(
    val id: String,
    val title: String,
    val category: String,
    val author: String,
    val image: ImageType,
)