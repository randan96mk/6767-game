package com.mkggames.puzzle2048.ui.theme

import androidx.compose.ui.graphics.Color

// ─── App Colors (Dark Cyberpunk Theme) ────────────────────────
val ScreenBackground = Color(0xFF060810)
val GridBackground = Color(0xFF111827)
val EmptyCellColor = Color(0xFF1A2236)
val HeaderTextColor = Color(0xFFE0E7EF)
val SubTextColor = Color(0xFF5A6A80)
val AccentCyan = Color(0xFF00F0FF)
val AccentMagenta = Color(0xFFFF2DCD)
val AccentGold = Color(0xFFFFD700)
val ScoreBoxBg = Color(0xFF0F1724)
val ButtonBg = Color(0xFF111827)
val OverlayBg = Color(0xE0060810)

// ─── Tile Colors (Classic 2048 Progression) ────────────────────

data class TileStyle(
    val background: Color,
    val textColor: Color,
    val glowColor: Color? = null
)

fun getTileStyle(value: Int): TileStyle {
    return when (value) {
        0    -> TileStyle(EmptyCellColor, Color.Transparent)
        2    -> TileStyle(Color(0xFF162033), Color(0xFF6AB0CC))
        4    -> TileStyle(Color(0xFF1A2E44), Color(0xFF7EC8E3))
        8    -> TileStyle(Color(0xFF0C4A6E), Color(0xFFE0F2FE), Color(0xFF0C4A6E))
        16   -> TileStyle(Color(0xFF0369A1), Color(0xFFFFFFFF), Color(0xFF0369A1))
        32   -> TileStyle(Color(0xFF0891B2), Color(0xFFFFFFFF), Color(0xFF0891B2))
        64   -> TileStyle(Color(0xFF06B6D4), Color(0xFF041E2E), Color(0xFF06B6D4))
        128  -> TileStyle(Color(0xFF00DDF7), Color(0xFF041E2E), Color(0xFF00F0FF))
        256  -> TileStyle(Color(0xFF00E5FF), Color(0xFF041E2E), Color(0xFF00F0FF))
        512  -> TileStyle(Color(0xFFC026D3), Color(0xFFFFFFFF), Color(0xFFFF2DCD))
        1024 -> TileStyle(Color(0xFFDB2777), Color(0xFFFFFFFF), Color(0xFFFF2DCD))
        2048 -> TileStyle(Color(0xFFF59E0B), Color(0xFF0A0E17), Color(0xFFFFD700))
        4096 -> TileStyle(Color(0xFFEF4444), Color(0xFFFFFFFF), Color(0xFFEF4444))
        8192 -> TileStyle(Color(0xFF00F0FF), Color(0xFF0A0E17), Color(0xFF00F0FF))
        else -> {
            if (value > 8192) {
                TileStyle(Color(0xFFFFD700), Color(0xFF0A0E17), Color(0xFFFFD700))
            } else {
                TileStyle(Color(0xFF37474F), Color(0xFFECEFF1))
            }
        }
    }
}

fun getTileFontSize(value: Int): Int {
    return when {
        value < 8 -> 34
        value < 100 -> 30
        value < 1000 -> 24
        value < 10000 -> 20
        else -> 16
    }
}
