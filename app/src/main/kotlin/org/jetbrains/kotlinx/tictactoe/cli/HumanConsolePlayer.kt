package org.jetbrains.kotlinx.tictactoe.cli

import org.jetbrains.kotlinx.tictactoe.api.Player
import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.GameState
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark

/**
 * Human player that prompts for moves via the terminal.
 */
class HumanConsolePlayer(
    override val name: String,
    private val ui: ConsoleUI
) : Player {
    override suspend fun selectMove(gameState: GameState): BoardPosition {
        ui.printMessage("\n$name's turn (${getCurrentMark(gameState)})")

        while (true) {
            val input = ui.promptInput("Enter position (row col, e.g., '0 1' for row 0, column 1)")
            val parts = input.trim().split(Regex("\\s+"))

            if (parts.size != 2) {
                ui.printError("Invalid input. Please enter two numbers separated by space.")
                continue
            }

            val row = parts[0].toIntOrNull()
            val col = parts[1].toIntOrNull()

            if (row == null || col == null) {
                ui.printError("Invalid numbers. Please enter valid integers.")
                continue
            }

            val position = BoardPosition(row, col)

            if (!gameState.isValidMove(position)) {
                ui.printError("Invalid move. Position ($row, $col) is not available.")
                continue
            }

            return position
        }
    }

    private fun getCurrentMark(gameState: GameState): PlayerMark? = gameState.nextToPlay
}
