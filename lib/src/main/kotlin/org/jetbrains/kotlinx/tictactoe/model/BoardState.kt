package org.jetbrains.kotlinx.tictactoe.model

/**
 * Represents an immutable snapshot of the game board state.
 *
 * This class provides a read-only view of the board with convenient access methods
 * for querying cell states.
 *
 * @property board A 2D list representing the board cells, where `board[row][column]` gives the cell state
 */
data class BoardState(val board: List<List<BoardCell>>) {
    /** The size of the board (number of rows/columns) */
    val size get() = board.size

    /**
     * Gets the cell at the specified position.
     *
     * @param position The board position to query
     * @return The [BoardCell] at that position
     * @throws IllegalArgumentException if the position is invalid
     */
    operator fun get(position: BoardPosition): BoardCell {
        position.requireValidForBoard(size)
        return board[position.row][position.column]
    }

    /**
     * Gets the cell at the specified row and column.
     *
     * @param row The row index
     * @param column The column index
     * @return The [BoardCell] at that position
     * @throws IllegalArgumentException if the position is invalid
     */
    operator fun get(row: Int, column: Int): BoardCell = this[BoardPosition(row, column)]

    /**
     * Iterates over all cells on the board.
     *
     * @param action A function called for each cell with (row, column, cell) parameters
     */
    fun forEach(action: (row: Int, col: Int, cell: BoardCell) -> Unit) {
        board.forEachIndexed { row, rowCells ->
            rowCells.forEachIndexed { col, cell ->
                action(row, col, cell)
            }
        }
    }

    /**
     * Returns a sequence of all board cells with their positions.
     *
     * @return A sequence of triples containing (row, column, cell)
     */
    fun asSequence(): Sequence<Triple<Int, Int, BoardCell>> = sequence {
        board.forEachIndexed { row, rowCells ->
            rowCells.forEachIndexed { col, cell ->
                yield(Triple(row, col, cell))
            }
        }
    }
}
