package com.game.winner6767.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.game.winner6767.data.ScoreRepository
import com.game.winner6767.game.Direction
import com.game.winner6767.game.GameEngine
import com.game.winner6767.game.GameStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameState(
    val grid: Array<IntArray> = GameEngine.createEmptyGrid(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val gameStatus: GameStatus = GameStatus.PLAYING,
    val hasWonBefore: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScoreRepository(application)

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var previousGrid: Array<IntArray>? = null
    private var previousScore: Int = 0

    init {
        val saved = repository.loadGame()
        if (saved != null) {
            val (grid, score) = saved
            val bestScore = repository.getBestScore()
            val won = GameEngine.hasWon(grid)
            val gameOver = GameEngine.isGameOver(grid)
            _state.value = GameState(
                grid = grid,
                score = score,
                bestScore = bestScore,
                gameStatus = when {
                    gameOver -> GameStatus.GAME_OVER
                    else -> GameStatus.PLAYING
                },
                hasWonBefore = won
            )
        } else {
            newGame()
        }
    }

    fun newGame() {
        val grid = GameEngine.initGame()
        previousGrid = null
        previousScore = 0
        repository.clearGame()
        _state.value = GameState(
            grid = grid,
            score = 0,
            bestScore = repository.getBestScore(),
            gameStatus = GameStatus.PLAYING,
            hasWonBefore = false
        )
        repository.saveGame(grid, 0)
    }

    fun onSwipe(direction: Direction) {
        val current = _state.value
        if (current.gameStatus == GameStatus.GAME_OVER) return
        if (current.gameStatus == GameStatus.WON) return

        val result = GameEngine.move(current.grid, direction)
        if (!result.moved) return

        // Save state for undo
        previousGrid = GameEngine.copyGrid(current.grid)
        previousScore = current.score

        val newScore = current.score + result.scoreGained
        GameEngine.spawnTile(result.grid)

        val bestScore = maxOf(newScore, current.bestScore)
        repository.saveBestScore(bestScore)
        repository.saveGame(result.grid, newScore)

        val won = GameEngine.hasWon(result.grid)
        val gameOver = GameEngine.isGameOver(result.grid)

        _state.value = current.copy(
            grid = result.grid,
            score = newScore,
            bestScore = bestScore,
            gameStatus = when {
                won && !current.hasWonBefore -> GameStatus.WON
                gameOver -> GameStatus.GAME_OVER
                else -> GameStatus.PLAYING
            },
            hasWonBefore = current.hasWonBefore || won
        )
    }

    fun continueAfterWin() {
        val current = _state.value
        _state.value = current.copy(
            gameStatus = GameStatus.PLAYING,
            hasWonBefore = true
        )
    }

    fun undo() {
        val prev = previousGrid ?: return
        val current = _state.value
        if (current.gameStatus == GameStatus.GAME_OVER || current.gameStatus == GameStatus.WON) return

        _state.value = current.copy(
            grid = prev,
            score = previousScore
        )
        repository.saveGame(prev, previousScore)
        previousGrid = null
    }
}
