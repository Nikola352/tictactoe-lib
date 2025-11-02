package org.jetbrains.kotlinx.tictactoe.api

import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.GameState

/**
 * Interface for implementing a tic-tac-toe player.
 *
 * Players can be human-controlled (waiting for input) or AI-controlled
 * (calculating the best move algorithmically).
 */
interface Player {
    /** The display name of the player */
    val name: String

    /**
     * Selects the next move for this player.
     *
     * This method is called when it's this player's turn.
     *
     * @param gameState The current state of the game
     * @return The position where the player wants to place their mark
     */
    fun selectMove(gameState: GameState): BoardPosition
}
