package org.jetbrains.kotlinx.tictactoe.internal

import org.jetbrains.kotlinx.tictactoe.model.PlayerMark

/**
 * Efficiently tracks the count of X and O marks in rows, columns, or diagonals.
 *
 * Used internally by [Board] for fast win condition checking.
 *
 * @param size The number of counters to maintain
 */
internal class MarkCounter(size: Int) {
    private val xCounts = IntArray(size)
    private val oCounts = IntArray(size)

    /** Increments the count for the given player mark at the specified index */
    fun increaseCount(index: Int, mark: PlayerMark) {
        when (mark) {
            PlayerMark.X -> xCounts[index]++
            PlayerMark.O -> oCounts[index]++
        }
    }

    /** Returns the count for the given player mark at the specified index */
    fun getCount(index: Int, mark: PlayerMark): Int = when (mark) {
        PlayerMark.X -> xCounts[index]
        PlayerMark.O -> oCounts[index]
    }

    /** Resets all counts to zero */
    fun reset() {
        xCounts.fill(0)
        oCounts.fill(0)
    }
}
