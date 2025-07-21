package com.jskaleel.fte.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.decorView.importantForAutofill =
                View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}
