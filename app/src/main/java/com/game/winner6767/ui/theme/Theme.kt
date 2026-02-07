package com.game.winner6767.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = lightColorScheme(
    primary = ButtonBackground,
    onPrimary = ButtonText,
    background = ScreenBackground,
    onBackground = DarkText,
    surface = ScreenBackground,
    onSurface = DarkText
)

@Composable
fun Game6767Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}
