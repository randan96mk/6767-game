package com.game.puzzle2048.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.game.puzzle2048.engine.GameEngine
import com.game.puzzle2048.engine.GameEngine.Direction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class GameStatus {
    PLAYING, WON, GAME_OVER
}

data class GameState(
    val grid: Array<IntArray> = GameEngine.createEmptyGrid(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val gameStatus: GameStatus = GameStatus.PLAYING,
    val hasWonBefore: Boolean = false,  // allows continue after winning
    val moveCount: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("game_2048", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    // Undo support: store previous state
    private var previousGrid: Array<IntArray>? = null
    private var previousScore: Int = 0
    private var canUndo = false

    private val _canUndoState = MutableStateFlow(false)
    val canUndoState: StateFlow<Boolean> = _canUndoState.asStateFlow()

    init {
        loadBestScore()
        startNewGame()
    }

    // ─── Public Actions ──────────────────────────────────────

    fun startNewGame() {
        var grid = GameEngine.createEmptyGrid()

        // Spawn 2 initial tiles
        val (grid1, _) = GameEngine.spawnTile(grid)
        val (grid2, _) = GameEngine.spawnTile(grid1)
        grid = grid2

        previousGrid = null
        canUndo = false
        _canUndoState.value = false

        _state.value = GameState(
            grid = grid,
            score = 0,
            bestScore = _state.value.bestScore,
            gameStatus = GameStatus.PLAYING,
            hasWonBefore = false,
            moveCount = 0
        )
    }

    fun onSwipe(direction: Direction) {
        val current = _state.value

        // Don't process moves if game is over (but allow if won and continuing)
        if (current.gameStatus == GameStatus.GAME_OVER) return
        if (current.gameStatus == GameStatus.WON && !current.hasWonBefore) return

        // Perform the move
        val moveResult = GameEngine.move(current.grid, direction)

        // If nothing moved, ignore
        if (!moveResult.moved) return

        // Save state for undo
        previousGrid = GameEngine.copyGrid(current.grid)
        previousScore = current.score
        canUndo = true
        _canUndoState.value = true

        // Spawn new tile
        val (newGrid, _) = GameEngine.spawnTile(moveResult.grid)
        val newScore = current.score + moveResult.score
        val newBest = maxOf(newScore, current.bestScore)

        // Update best score
        if (newBest > current.bestScore) {
            saveBestScore(newBest)
        }

        // Determine game status
        val status = when {
            !current.hasWonBefore && GameEngine.hasWon(newGrid) -> GameStatus.WON
            GameEngine.isGameOver(newGrid) -> GameStatus.GAME_OVER
            else -> GameStatus.PLAYING
        }

        _state.value = current.copy(
            grid = newGrid,
            score = newScore,
            bestScore = newBest,
            gameStatus = status,
            moveCount = current.moveCount + 1
        )
    }

    fun continueAfterWin() {
        val current = _state.value
        if (current.gameStatus == GameStatus.WON) {
            _state.value = current.copy(
                gameStatus = GameStatus.PLAYING,
                hasWonBefore = true
            )
        }
    }

    fun undo() {
        if (!canUndo || previousGrid == null) return

        val current = _state.value
        _state.value = current.copy(
            grid = previousGrid!!,
            score = previousScore,
            gameStatus = GameStatus.PLAYING,
            moveCount = current.moveCount - 1
        )

        canUndo = false
        previousGrid = null
        _canUndoState.value = false
    }

    // ─── Persistence ─────────────────────────────────────────

    private fun loadBestScore() {
        val best = prefs.getInt("best_score", 0)
        _state.value = _state.value.copy(bestScore = best)
    }

    private fun saveBestScore(score: Int) {
        prefs.edit().putInt("best_score", score).apply()
    }
}
