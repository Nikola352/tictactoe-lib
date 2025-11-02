package org.jetbrains.kotlinx.tictactoe.game

import org.jetbrains.kotlinx.tictactoe.api.GameEvent
import org.jetbrains.kotlinx.tictactoe.api.GameEventListener
import org.jetbrains.kotlinx.tictactoe.api.Player
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark

/**
 * Orchestrates a complete tic-tac-toe game between two players.
 *
 * This class manages the game loop, handling:
 * - Player turn management
 * - Move execution and validation
 * - Event notification to listeners
 * - Game completion detection
 *
 * Players are provided via factory functions to support scenarios where
 * player instances need to be recreated for each game (e.g., stateful AI players).
 *
 * Example usage:
 * ```
 * val runner = TicTacToeGameRunner(
 *     xPlayerProvider = { RandomAI("Bot X") },
 *     oPlayerProvider = { RandomAI("Bot O") },
 *     listener = ConsoleLogger(),
 *     boardSize = 3
 * )
 * runner.play()
 * ```
 *
 * @property xPlayerProvider Factory function that creates the X player
 * @property oPlayerProvider Factory function that creates the O player
 * @property listener Event listener that receives game state updates
 */
class TicTacToeGameRunner(
    private val xPlayerProvider: () -> Player,
    private val oPlayerProvider: () -> Player,
    private val listener: GameEventListener,
    boardSize: Int = 3,
) {
    private lateinit var xPlayer: Player
    private lateinit var oPlayer: Player

    private val game = TicTacToeGame(boardSize)

    /**
     * Starts and runs a complete game until completion.
     *
     * This method:
     * 1. Creates player instances from the provider functions
     * 2. Resets the game board
     * 3. Alternates turns between players until the game ends
     * 4. Handles invalid moves by notifying the listener and retrying
     * 5. Emits a [GameEvent.GameOver] event when the game concludes
     *
     * The game loop continues until either a player wins or the board is full (draw).
     */
    fun play() {
        xPlayer = xPlayerProvider()
        oPlayer = oPlayerProvider()

        game.reset()
        displayBoard()

        while (!game.isOver) {
            val player = if (game.turn == PlayerMark.X) xPlayer else oPlayer
            val move = player.selectMove(game.getState())

            try {
                game.playMove(move)
            } catch (e: IllegalArgumentException) {
                listener.onGameEvent(GameEvent.InvalidMove(e.message ?: "Illegal move"))
                continue
            }

            displayBoard()
        }

        listener.onGameEvent(GameEvent.GameOver(game.isDraw, game.winner))
    }

    /**
     * Notifies the listener of the current board state.
     *
     * Emits a [GameEvent.BoardUpdated] event with the current game state.
     */
    private fun displayBoard() {
        listener.onGameEvent(GameEvent.BoardUpdated(game.getState()))
    }
}
