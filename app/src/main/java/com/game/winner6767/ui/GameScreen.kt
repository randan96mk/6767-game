package com.game.winner6767.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.winner6767.game.Direction
import com.game.winner6767.ui.theme.ButtonBackground
import com.game.winner6767.ui.theme.ButtonText
import com.game.winner6767.ui.theme.DarkText
import com.game.winner6767.viewmodel.GameViewModel
import kotlin.math.abs

@Composable
fun GameScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()

    var dragAmountX by remember { mutableFloatStateOf(0f) }
    var dragAmountY by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "6767",
            color = DarkText,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Score cards row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ScoreCard(label = "SCORE", score = state.score)
            Spacer(modifier = Modifier.width(12.dp))
            ScoreCard(label = "BEST", score = state.bestScore)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { viewModel.undo() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBackground,
                    contentColor = ButtonText
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(text = "Undo", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { viewModel.newGame() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBackground,
                    contentColor = ButtonText
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(text = "New Game", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Game board with swipe detection & overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            dragAmountX = 0f
                            dragAmountY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragAmountX += dragAmount.x
                            dragAmountY += dragAmount.y
                        },
                        onDragEnd = {
                            val threshold = 50f
                            if (abs(dragAmountX) > abs(dragAmountY)) {
                                if (dragAmountX > threshold) {
                                    viewModel.onSwipe(Direction.RIGHT)
                                } else if (dragAmountX < -threshold) {
                                    viewModel.onSwipe(Direction.LEFT)
                                }
                            } else {
                                if (dragAmountY > threshold) {
                                    viewModel.onSwipe(Direction.DOWN)
                                } else if (dragAmountY < -threshold) {
                                    viewModel.onSwipe(Direction.UP)
                                }
                            }
                        }
                    )
                }
        ) {
            GameBoard(grid = state.grid)

            GameOverlay(
                gameStatus = state.gameStatus,
                score = state.score,
                onNewGame = { viewModel.newGame() },
                onContinue = { viewModel.continueAfterWin() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Swipe to play! Reach 6767!",
            color = DarkText,
            fontSize = 14.sp
        )
    }
}
