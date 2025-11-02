package org.jetbrains.kotlinx.tictactoe.players

import org.jetbrains.kotlinx.tictactoe.api.Player
import org.jetbrains.kotlinx.tictactoe.game.TicTacToeGame
import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.GameState
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark
import org.jetbrains.kotlinx.tictactoe.model.opposite
import kotlin.math.max
import kotlin.math.min

/**
 * A computer player that always selects the optimal move using the Minimax algorithm.
 */
class MinimaxPlayer(override val name: String) : Player {

    override suspend fun selectMove(gameState: GameState): BoardPosition {
        val maximizingPlayer = gameState.nextToPlay ?: error("Game is already over")

        var bestMove: BoardPosition? = null
        var bestScore = Int.MIN_VALUE

        for (move in gameState.getAvailableMoves()) {
            val simulated = TicTacToeGame(gameState).withMove(move)
            val score = minimax(
                simulated.getState(),
                depth = 0,
                isMaximizing = false,
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                maximizingPlayer = maximizingPlayer
            )
            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }

        return bestMove ?: error("No valid moves available")
    }

    private fun minimax(
        state: GameState,
        depth: Int,
        isMaximizing: Boolean,
        alpha: Int,
        beta: Int,
        maximizingPlayer: PlayerMark
    ): Int {
        var alphaVar = alpha
        var betaVar = beta

        if (state.isOver) {
            return evaluate(state, maximizingPlayer, depth)
        }

        val moves = state.getAvailableMoves()
        if (isMaximizing) {
            var best = Int.MIN_VALUE
            for (move in moves) {
                val simulated = TicTacToeGame(state).withMove(move)
                val score = minimax(
                    simulated.getState(),
                    depth + 1,
                    isMaximizing = false,
                    alpha = alphaVar,
                    beta = betaVar,
                    maximizingPlayer = maximizingPlayer
                )
                best = max(best, score)
                alphaVar = max(alphaVar, best)
                if (betaVar <= alphaVar) break
            }
            return best
        } else {
            var best = Int.MAX_VALUE
            for (move in moves) {
                val simulated = TicTacToeGame(state).withMove(move)
                val score = minimax(
                    simulated.getState(),
                    depth + 1,
                    isMaximizing = true,
                    alpha = alphaVar,
                    beta = betaVar,
                    maximizingPlayer = maximizingPlayer
                )
                best = min(best, score)
                betaVar = min(betaVar, best)
                if (betaVar <= alphaVar) break
            }
            return best
        }
    }

    /** Win is positive, loss is negative, but reward fewer moves. */
    private fun evaluate(state: GameState, maximizingPlayer: PlayerMark, depth: Int): Int = when (state.winner) {
        maximizingPlayer -> 10 - depth
        maximizingPlayer.opposite() -> depth - 10
        else -> 0 // draw

    }
}
