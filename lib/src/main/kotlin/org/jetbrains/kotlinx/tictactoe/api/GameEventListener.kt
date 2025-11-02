package org.jetbrains.kotlinx.tictactoe.api

/**
 * Listener interface for receiving game events.
 *
 * Implement this interface to respond to game state changes, such as updating
 * a UI or logging game progress.
 *
 * Example:
 * ```
 * class ConsoleLogger : GameEventListener {
 *     override fun onGameEvent(event: GameEvent) {
 *         when (event) {
 *             is GameEvent.BoardUpdated -> println("Board updated")
 *             is GameEvent.InvalidMove -> println("Invalid move: ${event.message}")
 *             is GameEvent.GameOver -> println("Winner: ${event.winner}")
 *         }
 *     }
 * }
 * ```
 */
interface GameEventListener {
    /**
     * Called when a game event occurs.
     *
     * @param event The event that occurred
     */
    fun onGameEvent(event: GameEvent)
}
