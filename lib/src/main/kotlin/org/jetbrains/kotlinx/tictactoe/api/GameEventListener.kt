package org.jetbrains.kotlinx.tictactoe.api

/**
 * Listener interface for receiving game events.
 *
 * Implement this interface to respond to game state changes, such as updating
 * a UI or logging game progress.
 *
 * The suspend modifier allows event handlers to perform asynchronous operations
 * such as updating UI with animations, sending events to a remote server etc.
 *
 * Example:
 * ```
 * class ConsoleLogger : GameEventListener {
 *     override suspend fun onGameEvent(event: GameEvent) {
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
     * This suspending function allows the listener to perform asynchronous
     * operations in response to events without blocking the game loop.
     *
     * @param event The event that occurred
     */
    suspend fun onGameEvent(event: GameEvent)
}
