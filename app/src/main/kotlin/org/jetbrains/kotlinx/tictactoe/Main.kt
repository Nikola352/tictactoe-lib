package org.jetbrains.kotlinx.tictactoe

import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlinx.tictactoe.api.GameEvent
import org.jetbrains.kotlinx.tictactoe.cli.ConsoleUI
import org.jetbrains.kotlinx.tictactoe.cli.GameSetup
import org.jetbrains.kotlinx.tictactoe.dsl.ticTacToeGame

fun main() = runBlocking {
    val ui = ConsoleUI()
    val gameSetup = GameSetup(ui)

    ui.printWelcome()
    val config = gameSetup.setupGame()

    val game = ticTacToeGame {
        playerX { config.playerX.selectMove(it) }
        playerO { config.playerO.selectMove(it) }
        onEvent { event ->
            when (event) {
                is GameEvent.BoardUpdated -> ui.printBoard(event.state)
                is GameEvent.InvalidMove -> ui.printError(event.message)
                is GameEvent.GameOver -> ui.printGameOver(event.isDraw, event.winner, config)
            }
        }
    }

    game.play()
}
