package com.jskaleel.fte.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.jskaleel.fte.core.model.CustomColors

private val LightColorScheme = lightColorScheme(
    primary = AppColor.Primary,
    onPrimary = AppColor.OnPrimary,
    secondary = AppColor.Secondary,
    onSecondary = AppColor.OnSecondary,
    background = AppColor.Background,
    onBackground = AppColor.OnBackground,
    surface = AppColor.Surface,
    onSurface = AppColor.OnSurface,
    error = AppColor.Error,
    onError = AppColor.OnError,
    primaryContainer = AppColor.PrimaryContainer,
    onPrimaryContainer = AppColor.OnPrimary,
)

fun getCustomColor(): CustomColors {
    return CustomColors(
        accent = AppColor.Accent,
        textPrimary = AppColor.TextPrimary,
        textSecondary = AppColor.TextSecondary,
        readingBackground = AppColor.Background,
    )
}

@Composable
fun FTEBooksTheme(
    content: @Composable () -> Unit
) {
    val customColors = getCustomColor()

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}