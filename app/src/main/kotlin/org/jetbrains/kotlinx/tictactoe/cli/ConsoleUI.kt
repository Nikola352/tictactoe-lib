package org.jetbrains.kotlinx.tictactoe.cli

import org.jetbrains.kotlinx.tictactoe.model.BoardCell
import org.jetbrains.kotlinx.tictactoe.model.GameState
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark

/**
 * Handles all terminal input/output with styled formatting.
 */
class ConsoleUI {
    private val reader = System.`in`.bufferedReader()

    fun printWelcome() {
        println("""
            
            ╔═══════════════════════════════════════╗
            ║                                       ║
            ║       X  TIC-TAC-TOE GAME  O          ║
            ║                                       ║
            ╚═══════════════════════════════════════╝
            
        """.trimIndent())
    }

    fun printBoard(state: GameState) {
        println("\n╭─────────────────────╮")
        println("│    Current Board    │")
        println("├─────────────────────┤")

        for (row in 0 until state.board.size) {
            print("│   ")
            for (col in 0 until state.board.size) {
                val symbol = when (val cell = state.board[row, col]) {
                    is BoardCell.Empty -> " "
                    is BoardCell.Occupied -> when (cell.playerMark) {
                        PlayerMark.X -> "X"
                        PlayerMark.O -> "O"
                    }
                }
                print(" $symbol ")
                if (col < state.board.size - 1) print(" │ ")
            }
            println("   │")

            if (row < state.board.size - 1) {
                println("│  ─────────────────  │")
            }
        }

        println("╰─────────────────────╯")
    }

    fun printGameOver(isDraw: Boolean, winner: PlayerMark?, gameConfig: GameConfiguration) {
        println("\n╔═══════════════════════════════════════╗")

        if (isDraw) {
            println("║                                       ║")
            println("║         🤝  GAME OVER - DRAW!         ║")
            println("║                                       ║")
        } else {
            val winnerSymbol = when (winner) {
                PlayerMark.X -> "X"
                PlayerMark.O -> "O"
                null -> "?"
            }
            println("║                                       ║")
            println("║       🎉  GAME OVER - $winnerSymbol WINS!         ║")
            println("║                                       ║")
        }

        println("╚═══════════════════════════════════════╝\n")

        val winnerName: String? = when (winner) {
            PlayerMark.X -> gameConfig.playerX.name
            PlayerMark.O -> gameConfig.playerO.name
            null -> null
        }
        if (winnerName != null) {
            println("Congrats, $winnerName!")
        }
    }

    private fun String.red() = "\u001B[31m$this\u001B[0m"

    fun printError(message: String) {
        println("X Error: $message".red())
    }

    fun printMessage(message: String) {
        println(message)
    }

    fun promptInput(prompt: String): String {
        print("➤ $prompt: ")
        return reader.readLine() ?: ""
    }

    fun promptPlayerType(): PlayerType {
        while (true) {
            println("\nSelect player type:")
            println("  1. Human")
            println("  2. Computer (Random)")
            println("  3. Computer (Minimax)")

            val input = promptInput("Enter choice (1-3)")

            return when (input.trim()) {
                "1" -> PlayerType.HUMAN
                "2" -> PlayerType.RANDOM
                "3" -> PlayerType.MINIMAX
                else -> {
                    printError("Invalid choice. Please enter 1, 2, or 3.")
                    continue
                }
            }
        }
    }
}
