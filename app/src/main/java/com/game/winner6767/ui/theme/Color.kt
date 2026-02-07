package com.game.winner6767.ui.theme

import androidx.compose.ui.graphics.Color

// Screen & Grid
val ScreenBackground = Color(0xFFFAF8EF)
val GridBackground = Color(0xFFBBADA0)
val EmptyCell = Color(0xFFCDC1B4)

// Tile colors by value
val TileColors = mapOf(
    0 to EmptyCell,
    2 to Color(0xFFEEE4DA),
    4 to Color(0xFFEDE0C8),
    8 to Color(0xFFF2B179),
    16 to Color(0xFFF59563),
    32 to Color(0xFFF67C5F),
    64 to Color(0xFFF65E3B),
    128 to Color(0xFFEDCF72),
    256 to Color(0xFFEDCC61),
    512 to Color(0xFFEDC850),
    1024 to Color(0xFFEDC53F),
    2048 to Color(0xFFEDC22E),
    4096 to Color(0xFF3C3A32),
    8192 to Color(0xFF3C3A32)
)

val DarkText = Color(0xFF776E65)
val LightText = Color(0xFFFFFFFF)

// Score card
val ScoreCardBackground = Color(0xFFBBADA0)
val ScoreCardText = Color(0xFFEEE4DA)
val ScoreCardValue = Color(0xFFFFFFFF)

// Buttons
val ButtonBackground = Color(0xFF8F7A66)
val ButtonText = Color(0xFFF9F6F2)

// Overlay
val OverlayBackground = Color(0xCCFAF8EF)

fun getTileBackground(value: Int): Color {
    return TileColors[value] ?: Color(0xFF3C3A32)
}

fun getTileTextColor(value: Int): Color {
    return if (value <= 4) DarkText else LightText
}
