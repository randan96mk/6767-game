package com.mkggames.puzzle2048.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Dark Theme Colors (Cyberpunk Neon) ────────────────────────
object DarkTheme {
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
    val SettingsBg = Color(0xFF0D1117)
    val ToggleTrackOn = Color(0xFF00F0FF)
    val ToggleTrackOff = Color(0xFF2A3444)
    val DialogGradientTop = Color(0xFF1A1A2E)
    val DialogGradientBottom = Color(0xFF0D1117)
}

// ─── Light Theme Colors (Clean & Warm) ─────────────────────────
object LightTheme {
    val ScreenBackground = Color(0xFFFAF8EF)
    val GridBackground = Color(0xFFBBADA0)
    val EmptyCellColor = Color(0xFFCDC1B4)
    val HeaderTextColor = Color(0xFF776E65)
    val SubTextColor = Color(0xFF998E82)
    val AccentCyan = Color(0xFF0099AA)
    val AccentMagenta = Color(0xFFCC2288)
    val AccentGold = Color(0xFFEDAC22)
    val ScoreBoxBg = Color(0xFFBBADA0)
    val ButtonBg = Color(0xFF8F7A66)
    val OverlayBg = Color(0xDDFAF8EF)
    val SettingsBg = Color(0xFFF5F0E6)
    val ToggleTrackOn = Color(0xFF0099AA)
    val ToggleTrackOff = Color(0xFFD4CAB8)
    val DialogGradientTop = Color(0xFFF5F0E6)
    val DialogGradientBottom = Color(0xFFEDE4D4)
}

// ─── Current theme accessor ────────────────────────────────────
data class AppColors(
    val screenBackground: Color,
    val gridBackground: Color,
    val emptyCellColor: Color,
    val headerTextColor: Color,
    val subTextColor: Color,
    val accentCyan: Color,
    val accentMagenta: Color,
    val accentGold: Color,
    val scoreBoxBg: Color,
    val buttonBg: Color,
    val overlayBg: Color,
    val settingsBg: Color,
    val toggleTrackOn: Color,
    val toggleTrackOff: Color,
    val dialogGradientTop: Color,
    val dialogGradientBottom: Color
)

fun getAppColors(isLight: Boolean): AppColors {
    return if (isLight) {
        AppColors(
            screenBackground = LightTheme.ScreenBackground,
            gridBackground = LightTheme.GridBackground,
            emptyCellColor = LightTheme.EmptyCellColor,
            headerTextColor = LightTheme.HeaderTextColor,
            subTextColor = LightTheme.SubTextColor,
            accentCyan = LightTheme.AccentCyan,
            accentMagenta = LightTheme.AccentMagenta,
            accentGold = LightTheme.AccentGold,
            scoreBoxBg = LightTheme.ScoreBoxBg,
            buttonBg = LightTheme.ButtonBg,
            overlayBg = LightTheme.OverlayBg,
            settingsBg = LightTheme.SettingsBg,
            toggleTrackOn = LightTheme.ToggleTrackOn,
            toggleTrackOff = LightTheme.ToggleTrackOff,
            dialogGradientTop = LightTheme.DialogGradientTop,
            dialogGradientBottom = LightTheme.DialogGradientBottom
        )
    } else {
        AppColors(
            screenBackground = DarkTheme.ScreenBackground,
            gridBackground = DarkTheme.GridBackground,
            emptyCellColor = DarkTheme.EmptyCellColor,
            headerTextColor = DarkTheme.HeaderTextColor,
            subTextColor = DarkTheme.SubTextColor,
            accentCyan = DarkTheme.AccentCyan,
            accentMagenta = DarkTheme.AccentMagenta,
            accentGold = DarkTheme.AccentGold,
            scoreBoxBg = DarkTheme.ScoreBoxBg,
            buttonBg = DarkTheme.ButtonBg,
            overlayBg = DarkTheme.OverlayBg,
            settingsBg = DarkTheme.SettingsBg,
            toggleTrackOn = DarkTheme.ToggleTrackOn,
            toggleTrackOff = DarkTheme.ToggleTrackOff,
            dialogGradientTop = DarkTheme.DialogGradientTop,
            dialogGradientBottom = DarkTheme.DialogGradientBottom
        )
    }
}

// ─── Legacy accessors (dark theme defaults) ────────────────────
val ScreenBackground = DarkTheme.ScreenBackground
val GridBackground = DarkTheme.GridBackground
val EmptyCellColor = DarkTheme.EmptyCellColor
val HeaderTextColor = DarkTheme.HeaderTextColor
val SubTextColor = DarkTheme.SubTextColor
val AccentCyan = DarkTheme.AccentCyan
val AccentMagenta = DarkTheme.AccentMagenta
val AccentGold = DarkTheme.AccentGold
val ScoreBoxBg = DarkTheme.ScoreBoxBg
val ButtonBg = DarkTheme.ButtonBg
val OverlayBg = DarkTheme.OverlayBg

// ─── Tile Colors (Dark Theme - Cyberpunk) ──────────────────────

data class TileStyle(
    val background: Color,
    val textColor: Color,
    val glowColor: Color? = null
)

fun getTileStyle(value: Int, isLight: Boolean = false): TileStyle {
    if (isLight) return getLightTileStyle(value)
    return when (value) {
        0    -> TileStyle(DarkTheme.EmptyCellColor, Color.Transparent)
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

// ─── Tile Colors (Light Theme - Classic 2048) ──────────────────

private fun getLightTileStyle(value: Int): TileStyle {
    return when (value) {
        0    -> TileStyle(LightTheme.EmptyCellColor, Color.Transparent)
        2    -> TileStyle(Color(0xFFEEE4DA), Color(0xFF776E65))
        4    -> TileStyle(Color(0xFFEDE0C8), Color(0xFF776E65))
        8    -> TileStyle(Color(0xFFF2B179), Color(0xFFF9F6F2))
        16   -> TileStyle(Color(0xFFF59563), Color(0xFFF9F6F2))
        32   -> TileStyle(Color(0xFFF67C5F), Color(0xFFF9F6F2))
        64   -> TileStyle(Color(0xFFF65E3B), Color(0xFFF9F6F2))
        128  -> TileStyle(Color(0xFFEDCF72), Color(0xFFF9F6F2))
        256  -> TileStyle(Color(0xFFEDCC61), Color(0xFFF9F6F2))
        512  -> TileStyle(Color(0xFFEDC850), Color(0xFFF9F6F2))
        1024 -> TileStyle(Color(0xFFEDC53F), Color(0xFFF9F6F2))
        2048 -> TileStyle(Color(0xFFEDC22E), Color(0xFFF9F6F2))
        4096 -> TileStyle(Color(0xFF3C3A32), Color(0xFFF9F6F2))
        8192 -> TileStyle(Color(0xFF3C3A32), Color(0xFFF9F6F2))
        else -> {
            if (value > 8192) {
                TileStyle(Color(0xFF3C3A32), Color(0xFFF9F6F2))
            } else {
                TileStyle(Color(0xFFCDC1B4), Color(0xFF776E65))
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
