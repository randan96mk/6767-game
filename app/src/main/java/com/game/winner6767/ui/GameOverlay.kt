package com.game.winner6767.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.winner6767.game.GameStatus
import com.game.winner6767.ui.theme.ButtonBackground
import com.game.winner6767.ui.theme.ButtonText
import com.game.winner6767.ui.theme.DarkText
import com.game.winner6767.ui.theme.OverlayBackground

@Composable
fun GameOverlay(
    gameStatus: GameStatus,
    score: Int,
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = gameStatus != GameStatus.PLAYING,
        enter = fadeIn()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(OverlayBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (gameStatus == GameStatus.WON) "You Win!" else "Game Over!",
                    color = DarkText,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )

                if (gameStatus == GameStatus.WON) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "6767 is the winner!",
                        color = DarkText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Score: $score",
                    color = DarkText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    if (gameStatus == GameStatus.WON) {
                        Button(
                            onClick = onContinue,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ButtonBackground,
                                contentColor = ButtonText
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Continue",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Button(
                        onClick = onNewGame,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonBackground,
                            contentColor = ButtonText
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "New Game",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
