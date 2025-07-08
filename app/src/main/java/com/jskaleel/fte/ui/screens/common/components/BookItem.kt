package com.jskaleel.fte.ui.screens.common.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
                                    SimpleOutlinedReadButton(
                                        text = "திற",
                                        onClick = onOpenClick,
                                        icon = Icons.AutoMirrored.Outlined.MenuBook,
                                        enabled = true
                                    )
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

@Composable
fun SimpleOutlinedReadButton(
    text: String = "திற",
    onClick: CallBack,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.AutoMirrored.Outlined.MenuBook,
    enabled: Boolean = true,
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = 8.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Colors from Material Theme
    val primaryColor = MaterialTheme.colorScheme.onPrimary
    val onSurface = MaterialTheme.colorScheme.onSurface

    // Animated colors
    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> onSurface.copy(alpha = 0.3f)
            isPressed -> primaryColor.copy(alpha = 0.8f)
            else -> primaryColor
        },
        animationSpec = tween(200),
        label = "border_color"
    )

    val textColor by animateColorAsState(
        targetValue = if (!enabled) onSurface.copy(alpha = 0.3f) else primaryColor,
        animationSpec = tween(200),
        label = "text_color"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) primaryColor.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(200),
        label = "background_color"
    )

    // Scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.labelSmall
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
            fontSize = 8.sp,
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
                showDownloadIcon = true,
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
                showDownloadIcon = true,
                onDownloadClick = {},
                downloaded = true,
                downloading = false
            )
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