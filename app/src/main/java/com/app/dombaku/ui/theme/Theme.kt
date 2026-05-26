package com.app.dombaku.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DombaKuLightColorScheme = lightColorScheme(
    primary            = GreenPrimary,
    onPrimary          = TextOnGreen,
    primaryContainer   = GreenContainer,
    onPrimaryContainer = GreenOnContainer,
    secondary          = GreenSecondary,
    onSecondary        = TextOnGreen,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = GreenPrimaryDark,
    tertiary           = GreenAccent,
    background         = BackgroundLight,
    onBackground       = TextPrimary,
    surface            = SurfaceLight,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceVariant,
    onSurfaceVariant   = TextSecondary,
    outline            = BorderColor,
    error              = StatusMati,
    onError            = Color.White,
)

private val DombaKuDarkColorScheme = darkColorScheme(
    primary            = GreenPrimaryDarkTheme,
    onPrimary          = GreenPrimaryDark,
    primaryContainer   = GreenPrimary,
    onPrimaryContainer = GreenContainer,
    secondary          = GreenAccent,
    onSecondary        = GreenPrimaryDark,
    background         = BackgroundDark,
    onBackground       = Color(0xFFE8F5E9),
    surface            = SurfaceDark,
    onSurface          = Color(0xFFE8F5E9),
    error              = Color(0xFFEF9A9A),
    onError            = Color(0xFF1A1A1A),
)

@Composable
fun DombaKuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DombaKuDarkColorScheme else DombaKuLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = GreenPrimaryDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = DombaKuTypography,
        content     = content
    )
}