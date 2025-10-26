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


private val LightColorScheme = lightColorScheme(
    primary = AppColor.Light.Primary,
    onPrimary = AppColor.Light.OnPrimary,
    primaryContainer = AppColor.Light.PrimaryContainer,
    onPrimaryContainer = AppColor.Light.OnPrimaryContainer,
    secondary = AppColor.Light.Secondary,
    onSecondary = AppColor.Light.OnSecondary,
    tertiary = AppColor.Light.Tertiary,
    onTertiary = AppColor.Light.OnTertiary,
    background = AppColor.Light.Background,
    onBackground = AppColor.Light.OnBackground,
    surface = AppColor.Light.Surface,
    onSurface = AppColor.Light.OnSurface,
    surfaceVariant = AppColor.Light.SurfaceVariant,
    onSurfaceVariant = AppColor.Light.OnSurfaceVariant,
    secondaryContainer = AppColor.Light.SecondaryContainer,
    onSecondaryContainer = AppColor.Light.OnSecondaryContainer,
    error = AppColor.Light.Error,
    onError = AppColor.Light.OnError,
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColor.Dark.Primary,
    onPrimary = AppColor.Dark.OnPrimary,
    primaryContainer = AppColor.Dark.PrimaryContainer,
    onPrimaryContainer = AppColor.Dark.OnPrimaryContainer,
    secondary = AppColor.Dark.Secondary,
    onSecondary = AppColor.Dark.OnSecondary,
    tertiary = AppColor.Dark.Tertiary,
    onTertiary = AppColor.Dark.OnTertiary,
    background = AppColor.Dark.Background,
    onBackground = AppColor.Dark.OnBackground,
    surface = AppColor.Dark.Surface,
    onSurface = AppColor.Dark.OnSurface,
    surfaceVariant = AppColor.Dark.SurfaceVariant,
    onSurfaceVariant = AppColor.Dark.OnSurfaceVariant,
    secondaryContainer = AppColor.Dark.SecondaryContainer,
    onSecondaryContainer = AppColor.Dark.OnSecondaryContainer,
    error = AppColor.Dark.Error,
    onError = AppColor.Dark.OnError,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = content
    )
}
