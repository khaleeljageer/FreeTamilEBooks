package com.jskaleel.fte.ui.screens.common.components


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.core.CallBack

enum class DownloadButtonState {
    DOWNLOAD,
    DOWNLOADING,
    DOWNLOADED
}

@Composable
fun AnimatedDownloadReadButton(
    modifier: Modifier = Modifier,
    state: DownloadButtonState,
    onDownloadClick: CallBack? = null,
    onOpenClick: CallBack? = null,
    onDeleteClick: CallBack? = null,
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(animationSpec = tween(200, delayMillis = 150)) togetherWith
                    fadeOut(animationSpec = tween(150))
        },
        label = "simple_content"
    ) { currentState ->
        when (currentState) {
            DownloadButtonState.DOWNLOAD -> {
                SimpleOutlinedReadButton(
                    modifier = modifier,
                    text = "பதிவிறக்கு",
                    onClick = onDownloadClick ?: {},
                    icon = Icons.Rounded.Download
                )
            }

            DownloadButtonState.DOWNLOADING -> {
                ProgressContent()
            }

            DownloadButtonState.DOWNLOADED -> {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SimpleOutlinedReadButton(
                        modifier = modifier,
                        text = "திற",
                        onClick = onOpenClick ?: {},
                        icon = Icons.AutoMirrored.Outlined.MenuBook
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (onDeleteClick != null) {
                        SimpleOutlinedReadButton(
                            modifier = modifier,
                            text = "நீக்கு",
                            onClick = onDeleteClick,
                            icon = Icons.Outlined.DeleteOutline
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleOutlinedReadButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: CallBack,
    icon: ImageVector? = null,
    borderWidth: Dp = 0.8.dp,
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(color = Color.Transparent, RoundedCornerShape(cornerRadius))
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun ProgressContent() {
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}