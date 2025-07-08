package com.jskaleel.fte.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

object AppColor {
    val Primary = Color(0xFFFFF5E0)
    val PrimaryContainer = Color(0xFFFFDBC3)

    val Secondary = Color(0xFFEE9E8E)
    val OnSecondary = Color(0xFF190933)

    val Background = Color(0xFFFFF5E0)
    val OnBackground = Color(0xFF190933)

    val Surface = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF190933)

    val OnPrimary = Color(0xFF190933)
    val TextPrimary = Color(0xFF190933)
    val TextSecondary = Color(0xFF5E4A58)

    val Accent = Color(0xFFEE9E8E)

    val Error = Color(0xFFD32F2F)
    val OnError = Color(0xFFFFFFFF)
}

val LocalCustomColors = compositionLocalOf { getCustomColor() }

val MaterialTheme.customColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current