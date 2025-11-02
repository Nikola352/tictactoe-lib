package org.jetbrains.kotlinx.tictactoe.dsl

import org.jetbrains.kotlinx.tictactoe.api.GameEvent
import org.jetbrains.kotlinx.tictactoe.api.GameEventListener
import org.jetbrains.kotlinx.tictactoe.api.Player
import org.jetbrains.kotlinx.tictactoe.game.TicTacToeGameRunner
import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.GameState

/**
 * Creates and configures a tic-tac-toe game using a DSL builder.
 *
 * @param boardSize The size of the board (default is 3 for standard 3x3)
 * @param block Configuration block for setting up players and event handlers
 * @return A configured [TicTacToeGameRunner] ready to start
 *
 * @throws IllegalArgumentException if players or event listener are not configured
 */
fun ticTacToeGame(
    boardSize: Int = 3,
    block: TicTacToeGameBuilder.() -> Unit
): TicTacToeGameRunner {
    return TicTacToeGameBuilder(boardSize).apply(block).build()
}

/**
 * Builder for configuring tic-tac-toe games using a DSL.
 *
 * This builder allows configuring:
 * - Player X with custom move selection logic
 * - Player O with custom move selection logic
 * - Event handlers for game state changes
 *
 * @property boardSize The size of the game board
 */
class TicTacToeGameBuilder(private val boardSize: Int = 3) {
    private var xPlayer: (() -> Player)? = null
    private var oPlayer: (() -> Player)? = null
    private var eventListener: GameEventListener? = null

    /**
     * Configures the X player.
     *
     * @param name The display name for player X (default: "Player X")
     * @param block A function that selects the player's next move given the game state
     */
    fun playerX(name: String = "Player X", block: (GameState) -> BoardPosition) {
        xPlayer = {
            object : Player {
                override val name = name
                override fun selectMove(gameState: GameState) = block(gameState)
            }
        }
    }

    /**
     * Configures the O player.
     *
     * @param name The display name for player O (default: "Player O")
     * @param block A function that selects the player's next move given the game state
     */
    fun playerO(name: String = "Player O", block: (GameState) -> BoardPosition) {
        oPlayer = {
            object : Player {
                override val name = name
                override fun selectMove(gameState: GameState) = block(gameState)
            }
        }
    }

    /**
     * Configures the event handler for game events.
     *
     * @param handler A function that processes [GameEvent]s
     */
    fun onEvent(handler: (GameEvent) -> Unit) {
        eventListener = object : GameEventListener {
            override fun onGameEvent(event: GameEvent) = handler(event)
        }
    }

    /**
     * Builds the configured game runner.
     *
     * @return A [TicTacToeGameRunner] with the configured players and event listener
     * @throws IllegalArgumentException if any required configuration is missing
     */
    fun build(): TicTacToeGameRunner {
        requireNotNull(xPlayer) { "Player X must be configured" }
        requireNotNull(oPlayer) { "Player O must be configured" }
        requireNotNull(eventListener) { "Event listener must be configured" }
        return TicTacToeGameRunner(xPlayer!!, oPlayer!!, eventListener!!, boardSize)
    }
}