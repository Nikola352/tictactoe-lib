package org.jetbrains.kotlinx.tictactoe.api

import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.GameState

/**
 * Interface for implementing a tic-tac-toe player.
 *
 * Players can be human-controlled (waiting for input) or AI-controlled
 * (calculating the best move algorithmically).
 *
 * The suspend modifier allows players to perform asynchronous operations
 * such as: waiting for user input from a UI, making network requests for remote players
 * or running computationally expensive AI algorithms without blocking
 */
interface Player {
    /** The display name of the player */
    val name: String

    /**
     * Selects the next move for this player.
     *
     * This suspending function is called when it's this player's turn.
     * It can suspend to wait for user input, network responses, or complete
     * background computations without blocking the calling thread.
     *
     * @param gameState The current state of the game
     * @return The position where the player wants to place their mark
     */
    suspend fun selectMove(gameState: GameState): BoardPosition
}
