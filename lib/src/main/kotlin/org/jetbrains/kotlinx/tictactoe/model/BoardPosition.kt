package org.jetbrains.kotlinx.tictactoe.model

/**
 * Represents a position on the tic-tac-toe board using zero-based indices.
 * 
 * @property row The row index (0-based)
 * @property column The column index (0-based)
 */
data class BoardPosition(
    val row: Int,
    val column: Int,
) {
    private fun isValidForBoard(size: Int): Boolean =
        row in 0 until size && column in 0 until size

    /**
     * Validates that this position is legal for a board of the given size.
     * 
     * @param size The size of the board
     * @throws IllegalArgumentException if the position is outside valid bounds
     */
    fun requireValidForBoard(size: Int) {
        require(isValidForBoard(size)) { "Position ($row, $column) is invalid for board size $size" }
    }
}
