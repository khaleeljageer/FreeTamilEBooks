package com.jskaleel.fte.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.R
import com.jskaleel.fte.core.model.ErrorState
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.consume
import com.jskaleel.fte.core.model.getImagePainter
import com.jskaleel.fte.core.utils.CallBack
import com.jskaleel.fte.ui.components.FteCard
import com.jskaleel.fte.ui.extensions.applyPlaceHolder
import com.jskaleel.fte.ui.extensions.isScrollingUp
import com.jskaleel.fte.ui.theme.FteTheme
import com.jskaleel.fte.ui.theme.dimension
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    uiState: HomeViewModelUiState,
    onDownloadClick: (Int) -> Unit,
) {
//    AppBarWithSearch {
//        Column {
//            AnimatedContent(
//                targetState = loading,
//                transitionSpec = {
//                    fadeIn(animationSpec = tween(220, delayMillis = 90)) with
//                            fadeOut(animationSpec = tween(90))
//                },
//                label = "book_list_animation"
//            ) { state ->
//                when (state) {
//                    true -> BookListLoaderContent()
//                    false -> BookListContent(
//                        onDownloadClick = onDownloadClick,
//                        books = books,
//                        errorState = state.error
//                    )
//                }
//            }
//        }
//    }

    when (uiState) {
        HomeViewModelUiState.Loading -> BookListLoaderContent()
        is HomeViewModelUiState.Success -> BookListContent(
            onDownloadClick = onDownloadClick,
            books = uiState.books,
            errorState = uiState.error
        )

        is HomeViewModelUiState.Error -> {
        }
    }
}

@Composable
fun BookListContent(
    onDownloadClick: (Int) -> Unit,
    books: List<BookUiModel>,
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
fun BookListLoaderContent() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimension.medium),
        contentPadding = PaddingValues(vertical = MaterialTheme.dimension.medium),
        modifier = Modifier
            .clipToBounds()
            .padding(horizontal = MaterialTheme.dimension.medium)
    ) {
        items(10) {
            FteCard(
                modifier = Modifier.height(180.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(160.dp)
                            .width(120.dp)
                            .aspectRatio(ratio = 0.75f)
                            .applyPlaceHolder(
                                shape = RoundedCornerShape(10.dp)
                            )
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
                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .fillMaxWidth()
                                    .applyPlaceHolder(
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.dimension.small))
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(20.dp)
                                    .applyPlaceHolder(
                                        shape = CircleShape
                                    ),
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(18.dp)
                                    .width(70.dp)
                                    .align(Alignment.Bottom)
                                    .applyPlaceHolder(
                                        shape = CircleShape
                                    )
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Box(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(30.dp)
                                    .applyPlaceHolder(
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
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
                            IconButton(
                                onClick = onDownloadClick
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_download),
                                    contentDescription = null
                                )
                            }
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
    FteTheme {
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

@Preview(showBackground = true)
@Composable
private fun BookListLoaderContentPreview() {
    FteTheme {
        BookListLoaderContent()
    }
}

data class BookUiModel(
    val title: String,
    val image: ImageType,
    val author: String,
    val category: String,
    val downloaded: Boolean,
    val progress: Boolean,
)