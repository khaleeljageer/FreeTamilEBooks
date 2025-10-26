package com.jskaleel.fte.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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

private val DarkColorScheme = darkColorScheme(
    primary = AppColorDark.Primary,
    onPrimary = AppColorDark.OnPrimary,
    secondary = AppColorDark.Secondary,
    onSecondary = AppColorDark.OnSecondary,
    background = AppColorDark.Background,
    onBackground = AppColorDark.OnBackground,
    surface = AppColorDark.Surface,
    onSurface = AppColorDark.OnSurface,
    error = AppColorDark.Error,
    onError = AppColorDark.OnError,
    primaryContainer = AppColorDark.PrimaryContainer,
    onPrimaryContainer = AppColorDark.OnPrimary,
)

@Composable
fun FTEBooksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    val customColors = if (darkTheme) {
        getCustomColorDark()
    } else {
        getCustomColor()
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.decorView.importantForAutofill =
                View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
