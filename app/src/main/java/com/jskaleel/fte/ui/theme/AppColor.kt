package com.jskaleel.fte.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.jskaleel.fte.core.model.CustomColors

@Suppress("detekt:MagicNumber")
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

@Suppress("detekt:MagicNumber")
object AppColorDark {
    val Primary = Color(0xFF3E2723)
    val PrimaryContainer = Color(0xFF5D4037)
    val Secondary = Color(0xFFD32F2F)
    val OnSecondary = Color(0xFFFFFFFF)
    val Background = Color(0xFF121212)
    val OnBackground = Color(0xFFFFFFFF)
    val Surface = Color(0xFF1E1E1E)
    val OnSurface = Color(0xFFFFFFFF)
    val OnPrimary = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFBDBDBD)
    val Accent = Color(0xFFD32F2F)
    val Error = Color(0xFFEF9A9A)
    val OnError = Color(0xFF000000)
}

fun getCustomColor(): CustomColors {
    return CustomColors(
        accent = AppColor.Accent,
        textPrimary = AppColor.TextPrimary,
        textSecondary = AppColor.TextSecondary,
        readingBackground = AppColor.Background,
    )
}

fun getCustomColorDark(): CustomColors {
    return CustomColors(
        accent = AppColorDark.Accent,
        textPrimary = AppColorDark.TextPrimary,
        textSecondary = AppColorDark.TextSecondary,
        readingBackground = AppColorDark.Background,
    )
}

val LocalCustomColors = compositionLocalOf { getCustomColor() }

val MaterialTheme.customColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current
