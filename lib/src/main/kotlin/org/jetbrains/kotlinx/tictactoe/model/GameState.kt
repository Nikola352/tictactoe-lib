package org.jetbrains.kotlinx.tictactoe.model

/**
 * Represents the complete state of a tic-tac-toe game at a specific point in time.
 *
 * This immutable snapshot includes the board state, turn information, and game outcome.
 *
 * @property board The current state of the game board
 * @property nextToPlay The player whose turn it is, or null if the game is over
 * @property isOver Whether the game has ended (either by win or draw)
 * @property isDraw Whether the game ended in a draw (no winner)
 * @property winner The winning player, or null if there's no winner yet, or it's a draw
 */
data class GameState(
    val board: BoardState,
    val nextToPlay: PlayerMark?,
    val isOver: Boolean,
    val isDraw: Boolean,
    val winner: PlayerMark?,
) {
    /**
     * Checks if a move at the given position would be valid.
     *
     * A move is valid if the game is not over and the cell is empty.
     *
     * @param position The position to check
     * @return true if placing a mark at this position would be a legal move
     */
    fun isValidMove(position: BoardPosition): Boolean {
        if (isOver) return false
        if (!position.isValidForBoard(board.size)) return false
        return board[position] == BoardCell.Empty
    }

    /**
     * Returns all positions where a valid move can be made.
     *
     * @return A list of all empty cell positions, or an empty list if the game is over
     */
    fun getAvailableMoves(): List<BoardPosition> {
        if (isOver) return emptyList()
        return (0 until board.size).flatMap { row ->
            (0 until board.size)
                .filter { col -> board[row, col] == BoardCell.Empty }
                .map { col -> BoardPosition(row, col) }
        }
    }
}
