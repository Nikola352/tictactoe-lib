package org.jetbrains.kotlinx.tictactoe.game

import org.jetbrains.kotlinx.tictactoe.internal.MarkCounter
import org.jetbrains.kotlinx.tictactoe.model.BoardCell
import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.BoardState
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark

/**
 * Internal mutable board representation with efficient win detection.
 *
 * This class maintains the actual game board state and uses counters to track
 * mark placements in rows, columns, and diagonals for O(1) win condition checking.
 *
 * The board supports:
 * - Placing marks at specific positions
 * - Efficient win detection using mark counters
 * - Creating immutable snapshots of the current state
 * - Resetting to empty state
 * - Restoring from a previous [BoardState]
 *
 * @property size The dimension of the square board (e.g., 3 for a 3x3 board)
 */
internal class Board(private val size: Int = 3) {
    private val board: Array<Array<BoardCell>> = Array(size) { Array(size) { BoardCell.Empty } }

    // Keep track of count of Xs and Os in each row, column and diagonal,
    // in order to quickly check for winning condition
    private val rowCounts = MarkCounter(size)
    private val columnCounts = MarkCounter(size)
    private val diagonalCounts = MarkCounter(2)

    // Total number of placed pieces, for quick game end detection
    private var totalMarks = 0

    var winner: PlayerMark? = null
        private set

    var isOver: Boolean = false
        private set

    /**
     * Restores a board from an existing [BoardState].
     *
     * This constructor validates the provided state and recalculates all internal
     * counters and game status indicators.
     *
     * @param board The board state to restore
     * @throws IllegalArgumentException if the board state has two winners (invalid state)
     */
    constructor(board: BoardState) : this(board.size) {
        for (row in 0 until size) {
            for (col in 0 until size) {
                val cell = board[row, col]
                this.board[row][col] = cell

                if (cell is BoardCell.Occupied) {
                    val position = BoardPosition(row, col)
                    updateCounts(position, cell.playerMark)
                }
            }
        }

        val xWon = checkWinner(PlayerMark.X)
        val oWon = checkWinner(PlayerMark.O)
        require(!(xWon && oWon)) { "Invalid board state: two winners" }
        winner = if (xWon) PlayerMark.X else if (oWon) PlayerMark.O else null

        isOver = winner != null || totalMarks == size * size
    }

    /**
     * Checks if the specified player has won the game.
     *
     * @param mark The player mark to check
     * @return true if the player has achieved a winning configuration
     */
    private fun checkWinner(mark: PlayerMark): Boolean {
        for (i in 0 until size) {
            if (rowCounts.getCount(i, mark) == size) return true
            if (columnCounts.getCount(i, mark) == size) return true
        }
        if (diagonalCounts.getCount(0, mark) == size) return true
        if (diagonalCounts.getCount(1, mark) == size) return true
        return false
    }

    /**
     * Creates an immutable snapshot of the current board state.
     *
     * @return A [BoardState] representing the current board configuration
     */
    fun createSnapshot(): BoardState {
        val boardList = board.map { row -> row.toList() }.toList()
        return BoardState(boardList)
    }

    /**
     * Resets the board to its initial empty state.
     *
     * Clears all cells, resets counters, and removes winner/game-over flags.
     */
    fun reset() {
        board.forEach { row -> row.fill(BoardCell.Empty) }
        totalMarks = 0
        rowCounts.reset()
        columnCounts.reset()
        diagonalCounts.reset()
        winner = null
        isOver = false
    }

    /**
     * Gets the cell at the specified position.
     *
     * @param position The board position to query
     * @return The [BoardCell] at that position
     * @throws IllegalArgumentException if the position is invalid for this board size
     */
    private fun getCell(position: BoardPosition): BoardCell {
        position.requireValidForBoard(size)
        return board[position.row][position.column]
    }

    /**
     * Places a player's mark at the specified position.
     *
     * This method validates the move, updates the board, increments counters,
     * and checks for win/draw conditions.
     *
     * @param position The position where the mark should be placed
     * @param mark The player mark to place
     * @throws IllegalArgumentException if the position is invalid or already occupied
     */
    fun place(position: BoardPosition, mark: PlayerMark) {
        require(getCell(position) == BoardCell.Empty) { "Illegal move. Cell taken." }

        board[position.row][position.column] = BoardCell.Occupied(mark)
        updateCounts(position, mark)

        if (checkWinner(position, mark)) {
            winner = mark
            isOver = true
        }

        // If the board is full and no winner is found, it's a draw
        if (totalMarks == size * size) {
            isOver = true
            winner = null
        }
    }

    /**
     * Updates internal counters after a mark is placed.
     *
     * @param position The position where the mark was placed
     * @param mark The mark that was placed
     */
    private fun updateCounts(position: BoardPosition, mark: PlayerMark) {
        totalMarks++
        rowCounts.increaseCount(position.row, mark)
        columnCounts.increaseCount(position.column, mark)
        if (position.isOnMainDiagonal()) {
            diagonalCounts.increaseCount(0, mark)
        }
        if (position.isOnMinorDiagonal()) {
            diagonalCounts.increaseCount(1, mark)
        }
    }

    /**
     * Efficiently checks if the move at the given position resulted in a win.
     *
     * Only checks the row, column, and diagonals affected by this specific move.
     *
     * @param position The position of the last placed mark
     * @param mark The mark that was placed
     * @return true if this move resulted in a win for the player
     */
    private fun checkWinner(position: BoardPosition, mark: PlayerMark): Boolean =
        (rowCounts.getCount(position.row, mark) == size)
                || (columnCounts.getCount(position.column, mark) == size)
                || (position.isOnMainDiagonal() && diagonalCounts.getCount(0, mark) == size)
                || (position.isOnMinorDiagonal() && diagonalCounts.getCount(1, mark) == size)

    /** Checks if this position is on the main diagonal (top-left to bottom-right) */
    private fun BoardPosition.isOnMainDiagonal(): Boolean = (row == column)

    /** Checks if this position is on the minor diagonal (top-right to bottom-left) */
    private fun BoardPosition.isOnMinorDiagonal(): Boolean = (row + column == size - 1)

    /**
     * Creates a copy of this board with the same state.
     *
     * @return A new Board instance with identical state to the current board
     */
    fun copy(): Board {
        val newBoard = Board(size)

        // Copy the board cells
        for (row in 0 until size) {
            for (col in 0 until size) {
                newBoard.board[row][col] = this.board[row][col]
            }
        }

        // Copy the counters
        newBoard.rowCounts.copyFrom(this.rowCounts)
        newBoard.columnCounts.copyFrom(this.columnCounts)
        newBoard.diagonalCounts.copyFrom(this.diagonalCounts)

        // Copy other state variables
        newBoard.totalMarks = this.totalMarks
        newBoard.winner = this.winner
        newBoard.isOver = this.isOver

        return newBoard
    }
}
