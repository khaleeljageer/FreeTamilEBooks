package com.jskaleel.fte.ui.extensions

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

fun Modifier.applyPlaceHolder(
    isLoading: Boolean = true,
    shape: Shape? = null
): Modifier = composed {
    this.placeholder(
        visible = isLoading,
        highlight = PlaceholderHighlight.shimmer(),
        shape = shape
    )
}

fun Modifier.screenPadding(
    start: Dp = 16.dp,
    top: Dp = 38.dp,
    end: Dp = 16.dp,
    bottom: Dp = 10.dp,
): Modifier = this.padding(
    start = start,
    top = top,
    end = end,
    bottom = bottom,
)