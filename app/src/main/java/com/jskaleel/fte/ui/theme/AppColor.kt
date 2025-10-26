package com.jskaleel.fte.ui.theme

import androidx.compose.ui.graphics.Color

@Suppress("detekt:MagicNumber")
object AppColor {
    object Light {
        val Primary = Color(0xFFFFF5E0)
        val PrimaryContainer = Color(0xFFFFDBC3)

        val Secondary = Color(0xFFEE9E8E)
        val OnSecondary = Color(0xFF190933)

        val Background = Color(0xFFFFF5E0)
        val OnBackground = Color(0xFF190933)

        val Surface = Color(0xFFFFFFFF)
        val OnSurface = Color(0xFF190933)

        val OnPrimary = Color(0xFF190933)

        val Error = Color(0xFFD32F2F)
        val OnError = Color(0xFFFFFFFF)
        val OnPrimaryContainer = Color(0xFF190933) // Deep purple text on light primary container
        val SecondaryContainer = Color(0xFFFFFFFF) // White fill for secondary elements, blending seamlessly
        val OnSecondaryContainer = Color(0xFF190933) // Deep purple on light secondary container
        val Tertiary = Color(0xFFAB47BC) // Vibrant purple accent for variety, evoking Tamil motifs
        val OnTertiary = Color(0xFFFFFFFF) // White for high contrast on tertiary
        val SurfaceVariant = Color(0xFFF5F5F5) // Soft gray for list items or variants
        val OnSurfaceVariant = Color(0xFF757575) // Medium gray text on surface variants
    }

    object Dark {
        val Primary = Color(0xFF2D1B3D)           // Deep purple-tinted background
        val PrimaryContainer = Color(0xFF3D2550)   // Slightly lighter purple container

        val Secondary = Color(0xFFFFB5A0)          // Warm peachy-pink (lighter than light theme)
        val OnSecondary = Color(0xFF2D1B3D)        // Deep purple on secondary

        val Background = Color(0xFF1A0F26)         // Very deep purple-black for reading comfort
        val OnBackground = Color(0xFFFFF5E0)       // Warm cream text (your light primary)

        val Surface = Color(0xFF2D1B3D)            // Matches primary for consistency
        val OnSurface = Color(0xFFFFF5E0)          // Warm cream text

        val OnPrimary = Color(0xFFFFF5E0)          // Warm cream on primary

        val Error = Color(0xFFFF6B6B)              // Softer red for dark theme
        val OnError = Color(0xFF1A0F26)            // Dark background on error

        val OnPrimaryContainer = Color(0xFFFFDBC3)      // Light peachy text on containers
        val SecondaryContainer = Color(0xFF3D2550)      // Purple container matching theme
        val OnSecondaryContainer = Color(0xFFFFDBC3)    // Light peachy on secondary container

        val Tertiary = Color(0xFFCE93D8)           // Lighter purple accent for dark theme
        val OnTertiary = Color(0xFF2D1B3D)         // Deep purple on tertiary

        val SurfaceVariant = Color(0xFF3D2550)     // Slightly elevated purple surface
        val OnSurfaceVariant = Color(0xFFD4C4B0)   // Muted warm beige for less important text
    }
}