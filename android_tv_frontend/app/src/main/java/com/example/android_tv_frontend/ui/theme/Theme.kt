package com.example.android_tv_frontend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Ocean Professional theme colors
private val OceanPrimary = Color(0xFF2563EB)
private val OceanSecondary = Color(0xFFF59E0B) // also used as success
private val OceanError = Color(0xFFEF4444)
private val OceanBackground = Color(0xFFF9FAFB)
private val OceanSurface = Color(0xFFFFFFFF)
private val OceanText = Color(0xFF111827)

private val LightColors = lightColorScheme(
    primary = OceanPrimary,
    onPrimary = Color.White,
    secondary = OceanSecondary,
    onSecondary = Color.Black,
    error = OceanError,
    background = OceanBackground,
    onBackground = OceanText,
    surface = OceanSurface,
    onSurface = OceanText
)

private val DarkColors = darkColorScheme(
    primary = OceanPrimary,
    onPrimary = Color.White,
    secondary = OceanSecondary,
    onSecondary = Color.Black,
    error = OceanError,
    background = Color(0xFF0B0F14),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFE5E7EB)
)

// PUBLIC_INTERFACE
@Composable
fun OceanTVTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme || isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
