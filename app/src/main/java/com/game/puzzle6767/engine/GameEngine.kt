package com.game.puzzle6767.engine

import kotlin.random.Random

/**
 * Classic 2048 Game Engine.
 *
 * - Spawn tiles: 2 (90%) or 4 (10%)
 * - Same tiles merge by doubling: 2+2=4, 4+4=8, etc.
 * - Single merge track: 2 → 4 → 8 → 16 → ... → 1024 → 2048
 * - Win when ANY tile reaches 2048
 * - Player can continue beyond 2048 for score chasing
 */
object GameEngine {

    const val GRID_SIZE = 4
    const val WIN_TARGET = 2048

    fun createEmptyGrid(): Array<IntArray> {
        return Array(GRID_SIZE) { IntArray(GRID_SIZE) { 0 } }
    }

    fun copyGrid(grid: Array<IntArray>): Array<IntArray> {
        return Array(GRID_SIZE) { row -> grid[row].copyOf() }
    }

    /**
     * Spawns a new tile on a random empty cell.
     * Spawn values: 2 (90%) or 4 (10%).
     */
    fun spawnTile(grid: Array<IntArray>): Pair<Array<IntArray>, Boolean> {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] == 0) emptyCells.add(r to c)
            }
        }
        if (emptyCells.isEmpty()) return grid to false

        val (r, c) = emptyCells[Random.nextInt(emptyCells.size)]
        val newGrid = copyGrid(grid)
        newGrid[r][c] = if (Random.nextFloat() < 0.9f) 2 else 4

        return newGrid to true
    }

    data class LineResult(
        val line: IntArray,
        val score: Int
    )

    /**
     * Slides a single line to the LEFT and merges adjacent equal tiles.
     *
     * Rules:
     *  1. Remove zeros (compact)
     *  2. Merge adjacent equal tiles (left to right)
     *  3. A merged tile cannot merge again in the same move
     *  4. Pad with zeros to maintain length
     *
     * Example: [2, 2, 4, 4] → [4, 8, 0, 0] (score +4 +8 = +12)
     * Example: [2, 2, 2, 2] → [4, 4, 0, 0] (NOT [8, 0, 0, 0])
     */
    fun slideLine(line: IntArray): LineResult {
        var score = 0
        val tiles = line.filter { it != 0 }.toMutableList()
        val merged = mutableListOf<Int>()
        var i = 0

        while (i < tiles.size) {
            if (i + 1 < tiles.size && tiles[i] == tiles[i + 1]) {
                val mergedValue = tiles[i] * 2
                merged.add(mergedValue)
                score += mergedValue
                i += 2
            } else {
                merged.add(tiles[i])
                i++
            }
        }

        while (merged.size < GRID_SIZE) merged.add(0)
        return LineResult(merged.toIntArray(), score)
    }

    enum class Direction { UP, DOWN, LEFT, RIGHT }

    data class MoveResult(
        val grid: Array<IntArray>,
        val score: Int,
        val moved: Boolean
    )

    fun move(grid: Array<IntArray>, direction: Direction): MoveResult {
        var totalScore = 0
        val newGrid = createEmptyGrid()
        var moved = false

        for (i in 0 until GRID_SIZE) {
            val line = extractLine(grid, i, direction)
            val result = slideLine(line)
            totalScore += result.score
            if (!line.contentEquals(result.line)) moved = true
            placeLine(newGrid, result.line, i, direction)
        }

        return MoveResult(newGrid, totalScore, moved)
    }

    private fun extractLine(grid: Array<IntArray>, index: Int, direction: Direction): IntArray {
        return when (direction) {
            Direction.LEFT -> grid[index].copyOf()
            Direction.RIGHT -> grid[index].reversed().toIntArray()
            Direction.UP -> IntArray(GRID_SIZE) { r -> grid[r][index] }
            Direction.DOWN -> IntArray(GRID_SIZE) { r -> grid[GRID_SIZE - 1 - r][index] }
        }
    }

    private fun placeLine(grid: Array<IntArray>, line: IntArray, index: Int, direction: Direction) {
        when (direction) {
            Direction.LEFT -> { for (c in 0 until GRID_SIZE) grid[index][c] = line[c] }
            Direction.RIGHT -> { for (c in 0 until GRID_SIZE) grid[index][GRID_SIZE - 1 - c] = line[c] }
            Direction.UP -> { for (r in 0 until GRID_SIZE) grid[r][index] = line[r] }
            Direction.DOWN -> { for (r in 0 until GRID_SIZE) grid[GRID_SIZE - 1 - r][index] = line[r] }
        }
    }

    fun hasWon(grid: Array<IntArray>): Boolean {
        for (r in 0 until GRID_SIZE)
            for (c in 0 until GRID_SIZE)
                if (grid[r][c] >= WIN_TARGET) return true
        return false
    }

    fun isGameOver(grid: Array<IntArray>): Boolean {
        for (r in 0 until GRID_SIZE)
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] == 0) return false
                if (c + 1 < GRID_SIZE && grid[r][c] == grid[r][c + 1]) return false
                if (r + 1 < GRID_SIZE && grid[r][c] == grid[r + 1][c]) return false
            }
        return true
    }
}
