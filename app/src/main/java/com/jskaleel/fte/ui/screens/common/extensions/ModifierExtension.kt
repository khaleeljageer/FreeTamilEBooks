package com.jskaleel.fte.ui.screens.common.extensions

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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