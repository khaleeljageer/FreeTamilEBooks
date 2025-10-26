package com.jskaleel.fte.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jskaleel.fte.R

val quicksand = FontFamily(
    Font(R.font.quicksand_light, FontWeight.Light),
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_semibold, FontWeight.SemiBold),
    Font(R.font.quicksand_bold, FontWeight.Bold)
)

val CustomTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        displayMedium = displayMedium.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        displaySmall = displaySmall.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        headlineLarge = headlineLarge.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        headlineMedium = headlineMedium.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        headlineSmall = headlineSmall.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        titleLarge = titleLarge.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        titleMedium = titleMedium.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        titleSmall = titleSmall.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        bodyLarge = bodyLarge.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        bodyMedium = bodyMedium.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        bodySmall = bodySmall.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        labelLarge = labelLarge.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        labelMedium = labelMedium.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        labelSmall = labelSmall.copy(
            fontFamily = quicksand,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
    )
}
