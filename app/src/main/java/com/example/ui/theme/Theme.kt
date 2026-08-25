package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyanLight,
    onPrimary = Navy900,
    primaryContainer = Navy700,
    onPrimaryContainer = CyanContainer,
    secondary = CyanAccent,
    onSecondary = Navy900,
    secondaryContainer = Navy800,
    onSecondaryContainer = Slate100,
    tertiary = StatusVerifiedGreen,
    background = Navy900,
    onBackground = Slate100,
    surface = Navy800,
    onSurface = Slate100,
    surfaceVariant = Navy700,
    onSurfaceVariant = Slate300,
    outline = Slate600
)

private val LightColorScheme = lightColorScheme(
    primary = CyanPrimary,
    onPrimary = Color.White,
    primaryContainer = CyanContainer,
    onPrimaryContainer = Navy900,
    secondary = Navy700,
    onSecondary = Color.White,
    secondaryContainer = Slate200,
    onSecondaryContainer = Navy900,
    tertiary = StatusVerifiedGreen,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate300
)

@Composable
fun LegalMetrologyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    LegalMetrologyTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

