package com.mkggames.puzzle2048.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.mkggames.puzzle2048.engine.GameEngine
import com.mkggames.puzzle2048.engine.GameEngine.Direction
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
    val moveCount: Int = 0,
    val mergedPositions: Set<Pair<Int, Int>> = emptySet(),
    val mergeGeneration: Int = 0
)

data class SettingsState(
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val lightThemeEnabled: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("game_2048", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    // Undo support: store previous state
    private var previousGrid: Array<IntArray>? = null
    private var previousScore: Int = 0
    private var canUndo = false

    private val _canUndoState = MutableStateFlow(false)
    val canUndoState: StateFlow<Boolean> = _canUndoState.asStateFlow()

    // Merge event for triggering sound/haptic
    private val _mergeEvent = MutableStateFlow<MergeEvent?>(null)
    val mergeEvent: StateFlow<MergeEvent?> = _mergeEvent.asStateFlow()

    data class MergeEvent(
        val maxMergedValue: Int,
        val mergeCount: Int,
        val generation: Int
    )

    private val _showHowToPlay = MutableStateFlow(false)
    val showHowToPlay: StateFlow<Boolean> = _showHowToPlay.asStateFlow()

    init {
        loadBestScore()
        loadSettings()
        startNewGame()

        // Show "How to Play" on first launch
        if (!prefs.getBoolean("has_launched_before", false)) {
            _showHowToPlay.value = true
            prefs.edit().putBoolean("has_launched_before", true).apply()
        }
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
            moveCount = 0,
            mergedPositions = emptySet(),
            mergeGeneration = 0
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

        // Emit merge event if merges occurred
        if (moveResult.mergedPositions.isNotEmpty()) {
            val maxValue = moveResult.mergedPositions.maxOf { (r, c) -> moveResult.grid[r][c] }
            _mergeEvent.value = MergeEvent(
                maxMergedValue = maxValue,
                mergeCount = moveResult.mergedPositions.size,
                generation = current.mergeGeneration + 1
            )
        }

        _state.value = current.copy(
            grid = newGrid,
            score = newScore,
            bestScore = newBest,
            gameStatus = status,
            moveCount = current.moveCount + 1,
            mergedPositions = moveResult.mergedPositions,
            mergeGeneration = current.mergeGeneration + 1
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
            moveCount = current.moveCount - 1,
            mergedPositions = emptySet()
        )

        canUndo = false
        previousGrid = null
        _canUndoState.value = false
    }

    fun consumeMergeEvent() {
        _mergeEvent.value = null
    }

    fun showHowToPlay() {
        _showHowToPlay.value = true
    }

    fun dismissHowToPlay() {
        _showHowToPlay.value = false
    }

    fun getHighestTile(): Int {
        val grid = _state.value.grid
        var max = 0
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                if (grid[r][c] > max) max = grid[r][c]
            }
        }
        return max
    }

    // ─── Settings ─────────────────────────────────────────────

    fun toggleSound() {
        val current = _settingsState.value
        _settingsState.value = current.copy(soundEnabled = !current.soundEnabled)
        saveSettings()
    }

    fun toggleHaptic() {
        val current = _settingsState.value
        _settingsState.value = current.copy(hapticEnabled = !current.hapticEnabled)
        saveSettings()
    }

    fun toggleLightTheme() {
        val current = _settingsState.value
        _settingsState.value = current.copy(lightThemeEnabled = !current.lightThemeEnabled)
        saveSettings()
    }

    // ─── Persistence ─────────────────────────────────────────

    private fun loadBestScore() {
        val best = prefs.getInt("best_score", 0)
        _state.value = _state.value.copy(bestScore = best)
    }

    private fun saveBestScore(score: Int) {
        prefs.edit().putInt("best_score", score).apply()
    }

    private fun loadSettings() {
        _settingsState.value = SettingsState(
            soundEnabled = prefs.getBoolean("sound_enabled", true),
            hapticEnabled = prefs.getBoolean("haptic_enabled", true),
            lightThemeEnabled = prefs.getBoolean("light_theme_enabled", false)
        )
    }

    private fun saveSettings() {
        val settings = _settingsState.value
        prefs.edit()
            .putBoolean("sound_enabled", settings.soundEnabled)
            .putBoolean("haptic_enabled", settings.hapticEnabled)
            .putBoolean("light_theme_enabled", settings.lightThemeEnabled)
            .apply()
    }
}
