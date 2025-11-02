package org.jetbrains.kotlinx.tictactoe.players

import org.jetbrains.kotlinx.tictactoe.api.Player
import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.GameState

/**
 * A computer player that picks the moves randomly.
 */
class RandomPlayer(override val name: String) : Player {
    override suspend fun selectMove(gameState: GameState): BoardPosition {
        return gameState.getAvailableMoves().random()
    }
}
