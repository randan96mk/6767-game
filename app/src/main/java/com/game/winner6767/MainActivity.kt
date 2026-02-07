package com.game.winner6767

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.game.winner6767.ui.GameScreen
import com.game.winner6767.ui.theme.Game6767Theme
import com.game.winner6767.ui.theme.ScreenBackground
import com.game.winner6767.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Game6767Theme {
                val viewModel: GameViewModel = viewModel()
                GameScreen(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ScreenBackground)
                        .safeDrawingPadding()
                )
            }
        }
    }
}
