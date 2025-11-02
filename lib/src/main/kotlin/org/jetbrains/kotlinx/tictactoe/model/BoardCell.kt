package org.jetbrains.kotlinx.tictactoe.model

/**
 * Represents the state of a single cell on the tic-tac-toe board.
 * 
 * A cell can be either:
 * - [Empty]: No player has placed a mark in this cell
 * - [Occupied]: A player has placed their mark in this cell
 */
sealed class BoardCell {
    /** Represents an empty cell that has no player mark */
    data object Empty : BoardCell()
    
    /**
     * Represents a cell occupied by a player's mark.
     * 
     * @property playerMark The mark ([PlayerMark.X] or [PlayerMark.O]) that occupies this cell
     */
    data class Occupied(val playerMark: PlayerMark) : BoardCell()
}
