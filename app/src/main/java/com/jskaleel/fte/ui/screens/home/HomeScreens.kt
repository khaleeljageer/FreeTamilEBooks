package com.jskaleel.fte.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.R
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.getImagePainter
import com.jskaleel.fte.core.utils.CallBack
import com.jskaleel.fte.ui.components.AppBarWithSearch
import com.jskaleel.fte.ui.components.FteCard
import com.jskaleel.fte.ui.extensions.applyPlaceHolder
import com.jskaleel.fte.ui.theme.FteTheme
import com.jskaleel.fte.ui.theme.dimension

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    loading: Boolean,
    onDownloadClick: (Int) -> Unit,
    books: List<BooksUiModel>,
) {
    AppBarWithSearch {
        Column {
            AnimatedContent(
                targetState = loading,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) with
                            fadeOut(animationSpec = tween(90))
                }
            ) { state ->
                when (state) {
                    true -> BookListLoaderContent()
                    false -> BookListContent(
                        onDownloadClick = onDownloadClick,
                        books = books
                    )
                }
            }
        }
    }
}

@Composable
fun BookListContent(
    onDownloadClick: (Int) -> Unit,
    books: List<BooksUiModel>
) {
    LazyColumn(
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
                downloadIcon = book.icon,
            )
        }
    }
}

@Composable
private fun BookListLoaderContent() {
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
    downloadIcon: ImageType,
) {
    FteCard(
        modifier = Modifier.height(180.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Image(
                painter = image.getImagePainter(),
                contentDescription = null,
                modifier = Modifier
                    .height(160.dp)
                    .width(120.dp)
                    .aspectRatio(ratio = 0.75f)
                    .clip(
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clipToBounds(),
                contentScale = ContentScale.Crop
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
                    Spacer(modifier = Modifier.height(MaterialTheme.dimension.small))
                    Text(
                        text = author,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryText(
                        label = category
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onDownloadClick
                    ) {
                        Icon(
                            painter = downloadIcon.getImagePainter(),
                            contentDescription = null
                        )
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
            image = ImageType.EMPTY,
            title = "Book Item Preview",
            author = "Author",
            category = "Category",
            downloadIcon = ImageType.ResourceImage(R.drawable.ic_download),
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

data class BooksUiModel(
    val title: String,
    val image: ImageType,
    val author: String,
    val category: String,
    val icon: ImageType
)