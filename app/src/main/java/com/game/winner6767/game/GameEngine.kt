package com.game.winner6767.game

import kotlin.random.Random

enum class Direction { UP, DOWN, LEFT, RIGHT }

enum class GameStatus { PLAYING, WON, GAME_OVER }

data class MoveResult(
    val grid: Array<IntArray>,
    val scoreGained: Int,
    val moved: Boolean
)

object GameEngine {

    const val GRID_SIZE = 4
    const val WIN_VALUE = 6767

    fun createEmptyGrid(): Array<IntArray> {
        return Array(GRID_SIZE) { IntArray(GRID_SIZE) }
    }

    fun initGame(): Array<IntArray> {
        val grid = createEmptyGrid()
        spawnTile(grid)
        spawnTile(grid)
        return grid
    }

    fun spawnTile(grid: Array<IntArray>): Boolean {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] == 0) emptyCells.add(r to c)
            }
        }
        if (emptyCells.isEmpty()) return false

        val (r, c) = emptyCells[Random.nextInt(emptyCells.size)]
        grid[r][c] = if (Random.nextFloat() < 0.9f) 2 else 4
        return true
    }

    fun move(grid: Array<IntArray>, direction: Direction): MoveResult {
        val newGrid = copyGrid(grid)
        var totalScore = 0
        var moved = false

        when (direction) {
            Direction.LEFT -> {
                for (r in 0 until GRID_SIZE) {
                    val result = slideLine(newGrid[r])
                    if (!result.line.contentEquals(newGrid[r])) moved = true
                    newGrid[r] = result.line
                    totalScore += result.score
                }
            }
            Direction.RIGHT -> {
                for (r in 0 until GRID_SIZE) {
                    val reversed = newGrid[r].reversedArray()
                    val result = slideLine(reversed)
                    val finalLine = result.line.reversedArray()
                    if (!finalLine.contentEquals(newGrid[r])) moved = true
                    newGrid[r] = finalLine
                    totalScore += result.score
                }
            }
            Direction.UP -> {
                for (c in 0 until GRID_SIZE) {
                    val column = IntArray(GRID_SIZE) { newGrid[it][c] }
                    val result = slideLine(column)
                    if (!result.line.contentEquals(column)) moved = true
                    for (r in 0 until GRID_SIZE) newGrid[r][c] = result.line[r]
                    totalScore += result.score
                }
            }
            Direction.DOWN -> {
                for (c in 0 until GRID_SIZE) {
                    val column = IntArray(GRID_SIZE) { newGrid[it][c] }.reversedArray()
                    val result = slideLine(column)
                    val finalCol = result.line.reversedArray()
                    val origCol = IntArray(GRID_SIZE) { newGrid[it][c] }
                    if (!finalCol.contentEquals(origCol)) moved = true
                    for (r in 0 until GRID_SIZE) newGrid[r][c] = finalCol[r]
                    totalScore += result.score
                }
            }
        }

        return MoveResult(newGrid, totalScore, moved)
    }

    data class LineResult(val line: IntArray, val score: Int)

    fun slideLine(line: IntArray): LineResult {
        // Step 1: Remove zeros
        val tiles = line.filter { it != 0 }.toMutableList()

        // Step 2: Merge adjacent equal tiles
        val merged = mutableListOf<Int>()
        var score = 0
        var i = 0
        while (i < tiles.size) {
            if (i + 1 < tiles.size && tiles[i] == tiles[i + 1]) {
                val mergedValue = tiles[i] * 2
                merged.add(mergedValue)
                score += mergedValue
                i += 2 // skip both tiles
            } else {
                merged.add(tiles[i])
                i++
            }
        }

        // Step 3: Pad with zeros
        while (merged.size < GRID_SIZE) {
            merged.add(0)
        }

        return LineResult(merged.toIntArray(), score)
    }

    fun hasWon(grid: Array<IntArray>): Boolean {
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] >= WIN_VALUE) return true
            }
        }
        return false
    }

    fun isGameOver(grid: Array<IntArray>): Boolean {
        // Check for empty cells
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] == 0) return false
            }
        }
        // Check for possible merges horizontally
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE - 1) {
                if (grid[r][c] == grid[r][c + 1]) return false
            }
        }
        // Check for possible merges vertically
        for (r in 0 until GRID_SIZE - 1) {
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] == grid[r + 1][c]) return false
            }
        }
        return true
    }

    fun copyGrid(grid: Array<IntArray>): Array<IntArray> {
        return Array(GRID_SIZE) { grid[it].copyOf() }
    }
}
