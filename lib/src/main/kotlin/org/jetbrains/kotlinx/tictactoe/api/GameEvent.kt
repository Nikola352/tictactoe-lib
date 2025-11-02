package org.jetbrains.kotlinx.tictactoe.api

import org.jetbrains.kotlinx.tictactoe.model.GameState
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark

/**
 * Events that occur during a tic-tac-toe game.
 *
 * These events allow observers to react to game state changes, invalid moves,
 * and game completion.
 */
sealed interface GameEvent {
    /**
     * Emitted after every valid move when the board state changes.
     *
     * @property state The updated game state after the move
     */
    data class BoardUpdated(val state: GameState) : GameEvent

    /**
     * Emitted when a player attempts an invalid move.
     *
     * @property message A description of why the move was invalid
     */
    data class InvalidMove(val message: String) : GameEvent

    /**
     * Emitted when the game ends.
     *
     * @property isDraw Whether the game ended in a draw
     * @property winner The winning player, or null if it's a draw
     */
    data class GameOver(val isDraw: Boolean, val winner: PlayerMark?) : GameEvent
}
