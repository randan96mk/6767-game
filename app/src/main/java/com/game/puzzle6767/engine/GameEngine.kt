package com.game.puzzle6767.engine

import kotlin.random.Random

/**
 * 6767 Puzzle Game Engine — Dual-Track Merge System.
 *
 * - Spawn tiles: 6 (90%) or 7 (10%)
 * - Same tiles merge by doubling: 6+6=12, 7+7=14, etc.
 * - Two parallel merge tracks that NEVER cross:
 *
 *   6-Track: 6 → 12 → 24 → 48 → 96 → 192 → 384 → 768 → 1536 → 3072 → 6144 → 12288
 *   7-Track: 7 → 14 → 28 → 56 → 112 → 224 → 448 → 896 → 1792 → 3584 → 7168
 *
 * - Win when ANY tile reaches >= 6767
 * - Player can continue beyond 6767 for score chasing
 */
object GameEngine {

    const val GRID_SIZE = 4
    const val WIN_TARGET = 6767

    fun createEmptyGrid(): Array<IntArray> {
        return Array(GRID_SIZE) { IntArray(GRID_SIZE) { 0 } }
    }

    fun copyGrid(grid: Array<IntArray>): Array<IntArray> {
        return Array(GRID_SIZE) { row -> grid[row].copyOf() }
    }

    /**
     * Spawns a new tile on a random empty cell.
     * Spawn values: 6 (90%) or 7 (10%).
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
        newGrid[r][c] = if (Random.nextFloat() < 0.9f) 6 else 7

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
     * Because 6-track and 7-track values never overlap, only same-track
     * tiles can merge. No additional track validation is needed.
     *
     * Example: [6, 6, 7, 7] → [12, 14, 0, 0] (score +12 +14 = +26)
     * Example: [6, 6, 6, 6] → [12, 12, 0, 0] (NOT [24, 0, 0, 0])
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
