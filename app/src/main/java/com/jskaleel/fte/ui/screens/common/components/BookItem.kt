package com.jskaleel.fte.ui.screens.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.getImagePainter
import com.jskaleel.fte.ui.screens.main.bookshelf.CategoryText
import com.jskaleel.fte.ui.theme.FTEBooksTheme
import com.jskaleel.fte.ui.theme.customColors
import com.jskaleel.fte.ui.theme.dimension


@Composable
fun BookItem(
    onOpenClick: CallBack,
    title: String,
    author: String,
    category: String,
    image: ImageType,
    showDownloadIcon: Boolean = true,
    onDownloadClick: CallBack? = null,
    downloaded: Boolean = false,
    downloading: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = MaterialTheme.dimension.small)
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable { onOpenClick() }
            .padding(all = MaterialTheme.dimension.small),
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
                    if (showDownloadIcon) {
                        when {
                            downloading -> {
                                IconButton(onClick = {}) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(30.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }

                            else -> {
                                if (downloaded) {
                                    IconButton(onClick = { onOpenClick() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                                            contentDescription = "Open Book"
                                        )
                                    }
                                } else {
                                    if (onDownloadClick != null) {
                                        DownloadIcon(
                                            onClick = onDownloadClick
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BookItemPreview() {
    FTEBooksTheme {
        BookItem(
            onOpenClick = {},
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            category = "Classic",
            image = ImageType.NetworkImage("https://www.gutenberg.org/cache/epub/64317/pg64317.cover.medium.jpg"),
            showDownloadIcon = true,
            onDownloadClick = {},
            downloaded = false,
            downloading = false
        )
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