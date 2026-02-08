package com.mkggames.puzzle2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkggames.puzzle2048.ui.GameScreen
import com.mkggames.puzzle2048.ui.theme.ScreenBackground
import com.mkggames.puzzle2048.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = ScreenBackground
            ) {
                val viewModel: GameViewModel = viewModel()
                GameScreen(viewModel = viewModel)
            }
        }
    }
}
