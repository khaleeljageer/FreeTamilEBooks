package com.jskaleel.fte.ui.screens.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.getImagePainter
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
                CategoryText(label = category)
                Spacer(modifier = Modifier.height(MaterialTheme.dimension.tiny))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.customColors.textPrimary,
                )
                Spacer(modifier = Modifier.height(MaterialTheme.dimension.small))
                Text(
                    text = author,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.customColors.textSecondary,
                )
                Spacer(modifier = Modifier.weight(1f))
                AnimatedDownloadReadButton(
                    state = when {
                        downloading -> DownloadButtonState.DOWNLOADING
                        downloaded -> DownloadButtonState.DOWNLOADED
                        else -> DownloadButtonState.DOWNLOAD
                    },
                    onDownloadClick = {
                        onDownloadClick?.invoke()
                    },
                    onOpenClick = {
                        onOpenClick()
                    }
                )
            }
        }
    }
}

@Composable
fun RecentBookItem(
    onClick: CallBack,
    title: String,
    lastRead: String,
    image: ImageType,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(all = MaterialTheme.dimension.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = image.getImagePainter(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(ratio = 0.75f, matchHeightConstraintsFirst = true)
                .clip(shape = RoundedCornerShape(8.dp))
                .clipToBounds()
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                maxLines = 2,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.customColors.textPrimary,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = lastRead,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                maxLines = 1,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                color = MaterialTheme.customColors.textSecondary,
            )
        }
    }
}

@Composable
private fun CategoryText(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.then(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.surface
            ),
            fontSize = 10.sp,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BookItemPreview() {
    FTEBooksTheme {
        Column {
            BookItem(
                onOpenClick = {},
                title = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                category = "Classic",
                image = ImageType.NetworkImage("https://www.gutenberg.org/cache/epub/64317/pg64317.cover.medium.jpg"),
                onDownloadClick = {},
                downloaded = false,
                downloading = false
            )

            BookItem(
                onOpenClick = {},
                title = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                category = "Classic",
                image = ImageType.NetworkImage("https://www.gutenberg.org/cache/epub/64317/pg64317.cover.medium.jpg"),
                onDownloadClick = {},
                downloaded = true,
                downloading = false
            )

            RecentBookItem(
                onClick = {},
                title = "The Great Gatsby",
                lastRead = "இன்று",
                image = ImageType.NetworkImage("https://www.gutenberg.org/cache/epub/64317/pg64317.cover.medium.jpg"),
            )
        }
    }
}
