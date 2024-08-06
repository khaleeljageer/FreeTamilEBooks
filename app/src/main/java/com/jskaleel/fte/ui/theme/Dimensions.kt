package com.jskaleel.fte.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Dimensions {
    val small: Dimension = Dimension(
        tiny = 4.dp,
        small = 8.dp,
        normal = 12.dp,
        medium = 16.dp,
        large = 20.dp,
        extraLarge = 24.dp,
        huge = 32.dp,
    )
}

data class Dimension(
    val tiny: Dp,
    val small: Dp,
    val normal: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp,
    val huge: Dp,
)

val LocalDimension = compositionLocalOf { Dimensions.small }

val MaterialTheme.dimension
    @Composable
    @ReadOnlyComposable
    get() = LocalDimension.current