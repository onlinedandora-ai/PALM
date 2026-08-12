package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PalmLightColorScheme = lightColorScheme(
    primary = PalmNavy,
    onPrimary = PalmWhite,
    secondary = PalmAccentBlue,
    onSecondary = PalmWhite,
    tertiary = PalmAlertAmber,
    background = PalmBackground,
    surface = PalmWhite,
    onBackground = PalmInk,
    onSurface = PalmInk,
    surfaceVariant = PalmSurfaceLight,
    onSurfaceVariant = PalmInk,
    outline = PalmLineGrey
)

private val PalmDarkColorScheme = darkColorScheme(
    primary = PalmAccentBlue,
    onPrimary = PalmWhite,
    secondary = PalmAlertAmber,
    onSecondary = PalmNavy,
    background = PalmNavy,
    surface = PalmInk,
    onBackground = PalmWhite,
    onSurface = PalmWhite,
    surfaceVariant = PalmNavy,
    outline = PalmLineGrey
)

@Composable
fun PalmTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) PalmDarkColorScheme else PalmLightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
