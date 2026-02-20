package com.mkggames.puzzle2048.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkggames.puzzle2048.engine.GameEngine
import com.mkggames.puzzle2048.engine.GameEngine.Direction
import com.mkggames.puzzle2048.ui.theme.*
import com.mkggames.puzzle2048.viewmodel.GameStatus
import com.mkggames.puzzle2048.viewmodel.GameViewModel
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── Header ──────────────────────────────
            GameHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // ─── Score Row ───────────────────────────
            ScoreRow(
                score = state.score,
                bestScore = state.bestScore
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ─── Action Buttons ──────────────────────
            ActionButtons(
                canUndo = canUndo,
                onUndo = { viewModel.undo() },
                onNewGame = { viewModel.startNewGame() },
                onHelp = { viewModel.showHowToPlay() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ─── Progress toward 2048 ────────────────
            GoalProgressBar(highestTile = viewModel.getHighestTile())

            Spacer(modifier = Modifier.height(8.dp))

            // ─── Game Board with Swipe ───────────────
            SwipeableGameBoard(
                grid = state.grid,
                onSwipe = { direction -> viewModel.onSwipe(direction) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ─── Info Footer ─────────────────────────
            GameFooter(moveCount = state.moveCount)
        }

        // ─── Overlays ────────────────────────────────
        AnimatedVisibility(
            visible = state.gameStatus == GameStatus.WON,
            enter = fadeIn(tween(400)),
            exit = fadeOut(tween(200))
        ) {
            WinOverlay(
                score = state.score,
                onContinue = { viewModel.continueAfterWin() },
                onNewGame = { viewModel.startNewGame() }
            )
        }

        AnimatedVisibility(
            visible = state.gameStatus == GameStatus.GAME_OVER,
            enter = fadeIn(tween(400)),
            exit = fadeOut(tween(200))
        ) {
            GameOverOverlay(
                score = state.score,
                onNewGame = { viewModel.startNewGame() }
            )
        }

        // ─── How to Play Dialog ──────────────────────
        if (showHowToPlay) {
            HowToPlayDialog(onDismiss = { viewModel.dismissHowToPlay() })
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// HEADER
// ═══════════════════════════════════════════════════════════════

@Composable
fun GameHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "2048",
            fontSize = 52.sp,
            fontWeight = FontWeight.Black,
            color = AccentCyan,
            letterSpacing = 4.sp
        )
        Text(
            text = "SWIPE • MERGE • REACH 2048",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = SubTextColor,
            letterSpacing = 2.sp
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// SCORE ROW
// ═══════════════════════════════════════════════════════════════

@Composable
fun ScoreRow(score: Int, bestScore: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScoreCard(
            label = "SCORE",
            value = score,
            accentColor = AccentCyan,
            modifier = Modifier.weight(1f)
        )
        ScoreCard(
            label = "BEST",
            value = bestScore,
            accentColor = AccentMagenta,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ScoreCard(label: String, value: Int, accentColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ScoreBoxBg)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = HeaderTextColor
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// ACTION BUTTONS
// ═══════════════════════════════════════════════════════════════

@Composable
fun ActionButtons(canUndo: Boolean, onUndo: () -> Unit, onNewGame: () -> Unit, onHelp: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Undo Button
        Button(
            onClick = onUndo,
            enabled = canUndo,
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonBg,
                contentColor = AccentCyan,
                disabledContainerColor = ButtonBg.copy(alpha = 0.5f),
                disabledContentColor = SubTextColor.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
        ) {
            Text("↩ UNDO", fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }

        // New Game Button
        Button(
            onClick = onNewGame,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentCyan.copy(alpha = 0.15f),
                contentColor = AccentCyan
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
        ) {
            Text("⟳ NEW GAME", fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }

        // How to Play Button
        Button(
            onClick = onHelp,
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonBg,
                contentColor = AccentGold
            ),
            shape = RoundedCornerShape(10.dp),
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
fun SwipeableGameBoard(grid: Array<IntArray>, onSwipe: (Direction) -> Unit) {
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
            .background(GridBackground)
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
                    onDragCancel = {
                        isDragging = false
                    }
                )
            }
            .padding(8.dp)
    ) {
        // Grid of tiles
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until 4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until 4) {
                        TileCell(
                            value = grid[row][col],
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
fun TileCell(value: Int, modifier: Modifier = Modifier) {
    val style = getTileStyle(value)
    val fontSize = getTileFontSize(value)

    // Pop animation for new/merged tiles
    val scale = remember(value) { Animatable(if (value != 0) 0.7f else 1f) }
    LaunchedEffect(value) {
        if (value != 0) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .then(
                if (style.glowColor != null && value != 0) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp),
                        ambientColor = style.glowColor,
                        spotColor = style.glowColor
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(10.dp))
            .background(style.background),
        contentAlignment = Alignment.Center
    ) {
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
fun GoalProgressBar(highestTile: Int) {
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
                color = if (reachedGoal) AccentGold else SubTextColor
            )
            Text(
                text = "Goal: 2048",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (reachedGoal) AccentGold else AccentCyan
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(ScoreBoxBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (reachedGoal) {
                            Brush.horizontalGradient(
                                colors = listOf(AccentCyan, AccentGold)
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(AccentCyan.copy(alpha = 0.5f), AccentCyan)
                            )
                        }
                    )
            )
        }
    }
}

@Composable
fun GameFooter(moveCount: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Merge matching tiles to reach 2048 and win!",
            fontSize = 11.sp,
            color = SubTextColor,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Moves: $moveCount",
            fontSize = 11.sp,
            color = SubTextColor.copy(alpha = 0.6f)
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// WIN OVERLAY
// ═══════════════════════════════════════════════════════════════

@Composable
fun WinOverlay(score: Int, onContinue: () -> Unit, onNewGame: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E),
                            Color(0xFF0D1117)
                        )
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "YOU WIN!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD700),
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You reached 2048!",
                fontSize = 16.sp,
                color = SubTextColor
            )
            Text(
                text = "Score: $score",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCyan
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Continue Button
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor = ScreenBackground
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("KEEP GOING", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // New Game Button
            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBg,
                    contentColor = SubTextColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("NEW GAME", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// GAME OVER OVERLAY
// ═══════════════════════════════════════════════════════════════

@Composable
fun GameOverOverlay(score: Int, onNewGame: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2D1117),
                            Color(0xFF0D1117)
                        )
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GAME OVER",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = AccentMagenta,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Final Score",
                fontSize = 14.sp,
                color = SubTextColor
            )
            Text(
                text = score.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = HeaderTextColor
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentMagenta,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("TRY AGAIN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// HOW TO PLAY DIALOG
// ═══════════════════════════════════════════════════════════════

@Composable
fun HowToPlayDialog(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E),
                            Color(0xFF0D1117)
                        )
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HOW TO PLAY",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = AccentCyan,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Goal section - most prominent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentGold.copy(alpha = 0.1f))
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "YOUR GOAL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reach the 2048 tile!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Steps
            HowToPlayStep(number = "1", text = "Swipe in any direction to slide all tiles")
            Spacer(modifier = Modifier.height(8.dp))
            HowToPlayStep(number = "2", text = "When two tiles with the same number touch, they merge into one!")
            Spacer(modifier = Modifier.height(8.dp))
            HowToPlayStep(number = "3", text = "Keep merging: 2 + 2 = 4, 4 + 4 = 8, ... up to 2048")
            Spacer(modifier = Modifier.height(8.dp))
            HowToPlayStep(number = "4", text = "Plan ahead — the board fills up fast!")

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "You can keep playing after reaching 2048 to chase an even higher score.",
                fontSize = 12.sp,
                color = SubTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor = ScreenBackground
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
private fun HowToPlayStep(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AccentCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCyan
            )
        }
        Text(
            text = text,
            fontSize = 14.sp,
            color = HeaderTextColor,
            modifier = Modifier.weight(1f)
        )
    }
}
