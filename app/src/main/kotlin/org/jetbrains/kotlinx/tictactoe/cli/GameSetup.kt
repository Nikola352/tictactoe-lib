package org.jetbrains.kotlinx.tictactoe.cli

import org.jetbrains.kotlinx.tictactoe.api.Player
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark
import org.jetbrains.kotlinx.tictactoe.players.MinimaxPlayer
import org.jetbrains.kotlinx.tictactoe.players.RandomPlayer

/**
 * Configuration for a game session.
 */
data class GameConfiguration(
    val playerX: Player,
    val playerO: Player
)

/**
 * Handles the initial game setup, including player selection.
 */
class GameSetup(private val ui: ConsoleUI) {
    /**
     * Prompts the user for game configuration and returns the setup.
     */
    fun setupGame(): GameConfiguration {
        val playerX = createPlayer(PlayerMark.X)
        val playerO = createPlayer(PlayerMark.O)
        return GameConfiguration(playerX, playerO)
    }

    private fun createPlayer(mark: PlayerMark): Player {
        val playerName = mark.toString()
        ui.printMessage("\n╭─────────────────────────────────╮")
        ui.printMessage("│  Configure Player $playerName             │")
        ui.printMessage("╰─────────────────────────────────╯")

        val name = ui.promptInput("Enter name for Player $playerName")
        val type = ui.promptPlayerType()

        return when (type) {
            PlayerType.HUMAN -> HumanConsolePlayer(name, ui)
            PlayerType.RANDOM -> RandomPlayer(name)
            PlayerType.MINIMAX -> MinimaxPlayer(name)
        }
    }
}

/**
 * Enum representing the available player types.
 */
enum class PlayerType {
    HUMAN, RANDOM, MINIMAX
}
