package com.game.winner6767.data

import android.content.Context
import android.content.SharedPreferences
import com.game.winner6767.game.GameEngine

class ScoreRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBestScore(): Int = prefs.getInt(KEY_BEST_SCORE, 0)

    fun saveBestScore(score: Int) {
        if (score > getBestScore()) {
            prefs.edit().putInt(KEY_BEST_SCORE, score).apply()
        }
    }

    fun saveGame(grid: Array<IntArray>, score: Int) {
        val sb = StringBuilder()
        for (r in 0 until GameEngine.GRID_SIZE) {
            for (c in 0 until GameEngine.GRID_SIZE) {
                if (sb.isNotEmpty()) sb.append(",")
                sb.append(grid[r][c])
            }
        }
        prefs.edit()
            .putString(KEY_GRID, sb.toString())
            .putInt(KEY_SCORE, score)
            .apply()
    }

    fun loadGame(): Pair<Array<IntArray>, Int>? {
        val gridStr = prefs.getString(KEY_GRID, null) ?: return null
        val values = gridStr.split(",").map { it.toIntOrNull() ?: 0 }
        if (values.size != GameEngine.GRID_SIZE * GameEngine.GRID_SIZE) return null

        val grid = Array(GameEngine.GRID_SIZE) { r ->
            IntArray(GameEngine.GRID_SIZE) { c ->
                values[r * GameEngine.GRID_SIZE + c]
            }
        }
        val score = prefs.getInt(KEY_SCORE, 0)
        return grid to score
    }

    fun clearGame() {
        prefs.edit()
            .remove(KEY_GRID)
            .remove(KEY_SCORE)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "game_6767_prefs"
        private const val KEY_BEST_SCORE = "best_score"
        private const val KEY_GRID = "current_grid"
        private const val KEY_SCORE = "current_score"
    }
}
