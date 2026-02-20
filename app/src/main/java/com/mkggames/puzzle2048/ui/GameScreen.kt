package com.mkggames.puzzle2048.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkggames.puzzle2048.engine.GameEngine
import androidx.compose.ui.window.Dialog
import com.mkggames.puzzle2048.audio.SoundManager
import com.mkggames.puzzle2048.engine.GameEngine.Direction
import com.mkggames.puzzle2048.ui.theme.*
import com.mkggames.puzzle2048.viewmodel.GameStatus
import com.mkggames.puzzle2048.viewmodel.GameViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ln

// ═══════════════════════════════════════════════════════════════
// MAIN GAME SCREEN
// ═══════════════════════════════════════════════════════════════

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    val canUndo by viewModel.canUndoState.collectAsState()
    val showHowToPlay by viewModel.showHowToPlay.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val mergeEvent by viewModel.mergeEvent.collectAsState()

    val isLight = settings.lightThemeEnabled
    val colors = getAppColors(isLight)

    var showSettings by remember { mutableStateOf(false) }

    // Sound manager + vibrator
    val view = LocalView.current
    val context = view.context
    val soundManager = remember { SoundManager(context) }
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val mgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            mgr.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    // Handle merge events: sound + haptic
    LaunchedEffect(mergeEvent) {
        val event = mergeEvent ?: return@LaunchedEffect
        if (settings.soundEnabled) {
            soundManager.playMergeSound(event.maxMergedValue)
        }
        if (settings.hapticEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        }
        viewModel.consumeMergeEvent()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.screenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── Header + Settings gear ──────────────
            GameHeader(colors = colors, onSettingsClick = { showSettings = true })

            Spacer(modifier = Modifier.height(16.dp))

            // ─── Score Row ───────────────────────────
            ScoreRow(score = state.score, bestScore = state.bestScore, colors = colors)

            Spacer(modifier = Modifier.height(12.dp))

            // ─── Action Buttons ──────────────────────
            ActionButtons(
                canUndo = canUndo,
                onUndo = { viewModel.undo() },
                onNewGame = { viewModel.startNewGame() },
                onHelp = { viewModel.showHowToPlay() },
                colors = colors
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ─── Progress toward 2048 ────────────────
            GoalProgressBar(highestTile = viewModel.getHighestTile(), colors = colors)

            Spacer(modifier = Modifier.height(8.dp))

            // ─── Game Board with Swipe ───────────────
            SwipeableGameBoard(
                grid = state.grid,
                isLight = isLight,
                colors = colors,
                mergedPositions = state.mergedPositions,
                mergeGeneration = state.mergeGeneration,
                onSwipe = { direction -> viewModel.onSwipe(direction) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ─── Info Footer ─────────────────────────
            GameFooter(moveCount = state.moveCount, colors = colors)
        }

        // ─── Overlays ────────────────────────────────
        AnimatedVisibility(
            visible = state.gameStatus == GameStatus.WON,
            enter = fadeIn(tween(400)),
            exit = fadeOut(tween(200))
        ) {
            WinOverlay(score = state.score, colors = colors,
                onContinue = { viewModel.continueAfterWin() },
                onNewGame = { viewModel.startNewGame() })
        }

        AnimatedVisibility(
            visible = state.gameStatus == GameStatus.GAME_OVER,
            enter = fadeIn(tween(400)),
            exit = fadeOut(tween(200))
        ) {
            GameOverOverlay(score = state.score, colors = colors,
                onNewGame = { viewModel.startNewGame() })
        }

        // ─── Settings Dialog ─────────────────────────
        if (showSettings) {
            SettingsDialog(
                settings = settings,
                colors = colors,
                onToggleSound = { viewModel.toggleSound() },
                onToggleHaptic = { viewModel.toggleHaptic() },
                onToggleTheme = { viewModel.toggleLightTheme() },
                onDismiss = { showSettings = false }
            )
        }

        // ─── How to Play Dialog ──────────────────────
        if (showHowToPlay) {
            HowToPlayDialog(colors = colors, onDismiss = { viewModel.dismissHowToPlay() })
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// HEADER
// ═══════════════════════════════════════════════════════════════

@Composable
fun GameHeader(colors: AppColors, onSettingsClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "2048",
                fontSize = 52.sp,
                fontWeight = FontWeight.Black,
                color = colors.accentCyan,
                letterSpacing = 4.sp
            )
            Text(
                text = "SWIPE • MERGE • REACH 2048",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = colors.subTextColor,
                letterSpacing = 2.sp
            )
        }
        // Gear / settings button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.buttonBg)
                .clickable { onSettingsClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\u2699",
                fontSize = 20.sp,
                color = colors.subTextColor
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// SCORE ROW
// ═══════════════════════════════════════════════════════════════

@Composable
fun ScoreRow(score: Int, bestScore: Int, colors: AppColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScoreCard("SCORE", score, colors.accentCyan, colors, Modifier.weight(1f))
        ScoreCard("BEST", bestScore, colors.accentMagenta, colors, Modifier.weight(1f))
    }
}

@Composable
fun ScoreCard(label: String, value: Int, accentColor: Color, colors: AppColors, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.scoreBoxBg)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colors.headerTextColor)
    }
}

// ═══════════════════════════════════════════════════════════════
// ACTION BUTTONS
// ═══════════════════════════════════════════════════════════════

@Composable
fun ActionButtons(canUndo: Boolean, onUndo: () -> Unit, onNewGame: () -> Unit, onHelp: () -> Unit, colors: AppColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onUndo,
            enabled = canUndo,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.buttonBg,
                contentColor = colors.accentCyan,
                disabledContainerColor = colors.buttonBg.copy(alpha = 0.5f),
                disabledContentColor = colors.subTextColor.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).height(44.dp)
        ) {
            Text("\u21A9 UNDO", fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        Button(
            onClick = onNewGame,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.accentCyan.copy(alpha = 0.15f),
                contentColor = colors.accentCyan
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).height(44.dp)
        ) {
            Text("\u27F3 NEW GAME", fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }

        // How to Play Button
        Button(
            onClick = onHelp,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.accentGold.copy(alpha = 0.15f),
                contentColor = colors.accentGold
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(48.dp)
                .height(44.dp)
        ) {
            Text("?", fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// GAME BOARD WITH SWIPE DETECTION
// ═══════════════════════════════════════════════════════════════

@Composable
fun SwipeableGameBoard(
    grid: Array<IntArray>,
    isLight: Boolean,
    colors: AppColors,
    mergedPositions: Set<Pair<Int, Int>> = emptySet(),
    mergeGeneration: Int = 0,
    onSwipe: (Direction) -> Unit
) {
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 40.dp.toPx() }

    var totalDragX by remember { mutableFloatStateOf(0f) }
    var totalDragY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.gridBackground)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                        isDragging = true
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                    },
                    onDragEnd = {
                        if (isDragging) {
                            val absX = abs(totalDragX)
                            val absY = abs(totalDragY)
                            if (absX > swipeThreshold || absY > swipeThreshold) {
                                val direction = if (absX > absY) {
                                    if (totalDragX > 0) Direction.RIGHT else Direction.LEFT
                                } else {
                                    if (totalDragY > 0) Direction.DOWN else Direction.UP
                                }
                                onSwipe(direction)
                            }
                        }
                        isDragging = false
                    },
                    onDragCancel = { isDragging = false }
                )
            }
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until 4) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until 4) {
                        TileCell(
                            value = grid[row][col],
                            isLight = isLight,
                            isMerged = (row to col) in mergedPositions,
                            mergeGeneration = mergeGeneration,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// SINGLE TILE CELL
// ═══════════════════════════════════════════════════════════════

@Composable
fun TileCell(
    value: Int,
    isLight: Boolean = false,
    isMerged: Boolean = false,
    mergeGeneration: Int = 0,
    modifier: Modifier = Modifier
) {
    val style = getTileStyle(value, isLight)
    val fontSize = getTileFontSize(value)

    // Pop animation for new tiles (appear from small)
    val spawnScale = remember(value) { Animatable(if (value != 0) 0.7f else 1f) }
    LaunchedEffect(value) {
        if (value != 0) {
            spawnScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    // Merge animation: scale overshoot 1.0 -> 1.25 -> 1.0
    val mergeScale = remember { Animatable(1f) }
    // Merge glow pulse: alpha 0 -> 1 -> 0
    val mergeGlow = remember { Animatable(0f) }

    LaunchedEffect(isMerged, mergeGeneration) {
        if (isMerged && value != 0) {
            // Run scale overshoot and glow pulse in parallel
            launch {
                mergeScale.snapTo(1f)
                mergeScale.animateTo(
                    targetValue = 1.25f,
                    animationSpec = tween(durationMillis = 120)
                )
                mergeScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                mergeGlow.snapTo(0.8f)
                mergeGlow.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            }
        }
    }

    val combinedScale = spawnScale.value * mergeScale.value
    val glowAlpha = mergeGlow.value
    val glowColor = style.glowColor ?: style.textColor.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = combinedScale
                scaleY = combinedScale
            }
            .then(
                if ((style.glowColor != null && value != 0) || glowAlpha > 0f) {
                    val elevation = if (glowAlpha > 0f) (8 + (16 * glowAlpha)).dp else 8.dp
                    val effectiveGlow = if (glowAlpha > 0f) glowColor.copy(alpha = glowAlpha) else style.glowColor ?: Color.Transparent
                    Modifier.shadow(
                        elevation = elevation,
                        shape = RoundedCornerShape(10.dp),
                        ambientColor = effectiveGlow,
                        spotColor = effectiveGlow
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(10.dp))
            .background(style.background),
        contentAlignment = Alignment.Center
    ) {
        // Glow overlay for merge flash
        if (glowAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        glowColor.copy(alpha = glowAlpha * 0.3f),
                        RoundedCornerShape(10.dp)
                    )
            )
        }
        if (value != 0) {
            Text(
                text = value.toString(),
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black,
                color = style.textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// FOOTER
// ═══════════════════════════════════════════════════════════════

@Composable
fun GoalProgressBar(highestTile: Int, colors: AppColors) {
    val targetPower = 11 // 2^11 = 2048
    val currentPower = if (highestTile >= 2) {
        (ln(highestTile.toDouble()) / ln(2.0)).toInt()
    } else 0
    val progress = (currentPower.toFloat() / targetPower).coerceIn(0f, 1f)
    val reachedGoal = highestTile >= GameEngine.WIN_TARGET

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (reachedGoal) "Goal reached!" else "Highest: $highestTile",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (reachedGoal) colors.accentGold else colors.subTextColor
            )
            Text(
                text = "Goal: 2048",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (reachedGoal) colors.accentGold else colors.accentCyan
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(colors.scoreBoxBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (reachedGoal) {
                            Brush.horizontalGradient(
                                colors = listOf(colors.accentCyan, colors.accentGold)
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(colors.accentCyan.copy(alpha = 0.5f), colors.accentCyan)
                            )
                        }
                    )
            )
        }
    }
}

@Composable
fun GameFooter(moveCount: Int, colors: AppColors) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Merge matching tiles to reach 2048 and win!",
            fontSize = 11.sp,
            color = colors.subTextColor,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text("Moves: $moveCount", fontSize = 11.sp, color = colors.subTextColor.copy(alpha = 0.6f))
    }
}

// ═══════════════════════════════════════════════════════════════
// WIN OVERLAY
// ═══════════════════════════════════════════════════════════════

@Composable
fun WinOverlay(score: Int, colors: AppColors, onContinue: () -> Unit, onNewGame: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(colors.overlayBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(colors.dialogGradientTop, colors.dialogGradientBottom)))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("\uD83C\uDF89", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("YOU WIN!", fontSize = 36.sp, fontWeight = FontWeight.Black, color = colors.accentGold, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("You reached 2048!", fontSize = 16.sp, color = colors.subTextColor)
            Text("Score: $score", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.accentCyan)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentCyan, contentColor = colors.screenBackground),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("KEEP GOING", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(containerColor = colors.buttonBg, contentColor = colors.subTextColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("NEW GAME", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// GAME OVER OVERLAY
// ═══════════════════════════════════════════════════════════════

@Composable
fun GameOverOverlay(score: Int, colors: AppColors, onNewGame: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(colors.overlayBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(colors.dialogGradientTop, colors.dialogGradientBottom)))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("GAME OVER", fontSize = 32.sp, fontWeight = FontWeight.Black, color = colors.accentMagenta, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Final Score", fontSize = 14.sp, color = colors.subTextColor)
            Text(score.toString(), fontSize = 36.sp, fontWeight = FontWeight.Black, color = colors.headerTextColor)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMagenta, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("TRY AGAIN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// SETTINGS DIALOG
// ═══════════════════════════════════════════════════════════════

@Composable
fun SettingsDialog(
    settings: com.mkggames.puzzle2048.viewmodel.SettingsState,
    colors: AppColors,
    onToggleSound: () -> Unit,
    onToggleHaptic: () -> Unit,
    onToggleTheme: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(colors.dialogGradientTop, colors.dialogGradientBottom)))
                .padding(24.dp)
        ) {
            Text(
                "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.headerTextColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))

            SettingsToggleRow(
                label = "Sound Effects",
                icon = "\uD83D\uDD0A",
                checked = settings.soundEnabled,
                onToggle = onToggleSound,
                colors = colors
            )
            Spacer(modifier = Modifier.height(12.dp))
            SettingsToggleRow(
                label = "Haptic Feedback",
                icon = "\uD83D\uDCF3",
                checked = settings.hapticEnabled,
                onToggle = onToggleHaptic,
                colors = colors
            )
            Spacer(modifier = Modifier.height(12.dp))
            SettingsToggleRow(
                label = "Light Theme",
                icon = "\u2600\uFE0F",
                checked = settings.lightThemeEnabled,
                onToggle = onToggleTheme,
                colors = colors
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accentCyan,
                    contentColor = colors.screenBackground
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// HOW TO PLAY DIALOG
// ═══════════════════════════════════════════════════════════════

@Composable
fun HowToPlayDialog(colors: AppColors, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.overlayBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colors.dialogGradientTop, colors.dialogGradientBottom)
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HOW TO PLAY",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = colors.accentCyan,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Goal section - most prominent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.accentGold.copy(alpha = 0.1f))
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "YOUR GOAL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentGold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reach the 2048 tile!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.accentGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Steps
            HowToPlayStep(number = "1", text = "Swipe in any direction to slide all tiles", colors = colors)
            Spacer(modifier = Modifier.height(8.dp))
            HowToPlayStep(number = "2", text = "When two tiles with the same number touch, they merge into one!", colors = colors)
            Spacer(modifier = Modifier.height(8.dp))
            HowToPlayStep(number = "3", text = "Keep merging: 2 + 2 = 4, 4 + 4 = 8, ... up to 2048", colors = colors)
            Spacer(modifier = Modifier.height(8.dp))
            HowToPlayStep(number = "4", text = "Plan ahead \u2014 the board fills up fast!", colors = colors)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "You can keep playing after reaching 2048 to chase an even higher score.",
                fontSize = 12.sp,
                color = colors.subTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accentCyan,
                    contentColor = colors.screenBackground
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("GOT IT!", fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun HowToPlayStep(number: String, text: String, colors: AppColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.accentCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accentCyan
            )
        }
        Text(
            text = text,
            fontSize = 14.sp,
            color = colors.headerTextColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SettingsToggleRow(
    label: String,
    icon: String,
    checked: Boolean,
    onToggle: () -> Unit,
    colors: AppColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.screenBackground.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colors.headerTextColor,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.toggleTrackOn,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = colors.toggleTrackOff
            )
        )
    }
}
