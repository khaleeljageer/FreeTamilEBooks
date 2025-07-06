package com.jskaleel.fte.ui.screens.main.bookshelf

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.getImagePainter
import com.jskaleel.fte.ui.screens.common.extensions.isScrollingUp
import com.jskaleel.fte.ui.theme.FTEBooksTheme
import com.jskaleel.fte.ui.theme.customColors
import com.jskaleel.fte.ui.theme.dimension
import kotlinx.coroutines.launch

@Composable
fun BookListContent(
    onDownloadClick: (Int) -> Unit,
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
            itemsIndexed(books) { index, book ->
                BookItem(
                    onDownloadClick = { onDownloadClick(index) },
                    image = book.image,
                    title = book.title,
                    author = book.author,
                    category = book.category,
                    downloaded = book.downloaded,
                    downloading = book.downloading,
                    progress = book.progress,
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
fun BookItem(
    onDownloadClick: CallBack,
    title: String,
    author: String,
    category: String,
    image: ImageType,
    downloaded: Boolean,
    downloading: Boolean,
    progress: Int,
) {
    Row(
        modifier = Modifier
            .padding(all = MaterialTheme.dimension.normal)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
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
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth(),
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
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.customColors.textPrimary,
                )
                Spacer(modifier = Modifier.height(MaterialTheme.dimension.tiny))
                Text(
                    text = author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.customColors.textSecondary,
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

                AnimatedContent(targetState = downloading, label = "progress_anim") { state ->
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

@Composable
fun DownloadIcon(
    onClick: CallBack
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Rounded.Download,
            contentDescription = "Download",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
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
                title = "Book Item Preview",
                author = "Author",
                category = "Category",
                image = ImageType.EMPTY,
                downloaded = true,
                downloading = false,
                progress = 0,
            )

            BookItem(
                onDownloadClick = { },
                title = "Book Item Preview",
                author = "Author",
                category = "Category",
                image = ImageType.EMPTY,
                downloaded = false,
                downloading = false,
                progress = 0,
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
    val progress: Int,
)