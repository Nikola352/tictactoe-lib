package org.jetbrains.kotlinx.tictactoe.game

import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.GameState
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark
import org.jetbrains.kotlinx.tictactoe.model.opposite

/**
 * Main controller for a tic-tac-toe game.
 *
 * This class manages the game lifecycle including:
 * - Turn management
 * - Move validation and execution
 * - Win/draw detection
 * - Game state snapshots
 *
 * Use this class if you want to implement a custom driver code.
 * However, using [TicTacToeGameRunner] is recommended for most use cases.
 *
 * Example usage:
 * ```
 * val game = TicTacToeGame(boardSize = 3)
 * game.playMove(BoardPosition(0, 0)) // X plays
 * game.playMove(BoardPosition(1, 1)) // O plays
 * val state = game.getState()
 * println("Next to play: ${state.nextToPlay}")
 * ```
 *
 * @constructor Creates a new game with the specified board and initial turn
 * @property turn The player whose turn it is to play
 */
class TicTacToeGame private constructor(
    private val board: Board,
    initialTurn: PlayerMark = PlayerMark.X
) {
    var turn: PlayerMark = initialTurn
        private set

    /** Whether the game has ended (either by win or draw) */
    val isOver: Boolean get() = board.isOver

    /** The winning player, or null if no winner yet or it's a draw */
    val winner: PlayerMark? get() = board.winner

    /** Whether the game ended in a draw (board full with no winner) */
    val isDraw: Boolean get() = board.isOver && board.winner == null

    /**
     * Creates a new game with an empty board.
     *
     * @param boardSize The size of the board (default is 3 for a standard 3x3 game)
     */
    constructor(boardSize: Int = 3) : this(Board(boardSize))

    /**
     * Creates a game from an existing game state.
     *
     * This constructor allows resuming a game from a previous state.
     *
     * @param gameState The game state to restore
     */
    constructor(gameState: GameState) : this(
        Board(gameState.board),
        gameState.nextToPlay ?: PlayerMark.X
    )

    /**
     * Executes a move for the current player.
     *
     * The move is validated and if successful, the turn switches to the other player.
     * The game state is updated to reflect win/draw conditions.
     *
     * @param move The position where the current player wants to place their mark
     * @throws IllegalArgumentException if the cell is already occupied
     */
    fun playMove(move: BoardPosition) {
        board.place(move, turn)
        turn = turn.opposite()
    }

    /**
     * Returns a new copy of the game with the specified move played.
     *
     * This method does not modify the current game instance.
     *
     * @param move The position where the current player wants to place their mark
     * @return A new TicTacToeGame instance with the move played
     * @throws IllegalArgumentException if the cell is already occupied
     */
    fun withMove(move: BoardPosition): TicTacToeGame {
        val newBoard = board.copy()
        newBoard.place(move, turn)
        return TicTacToeGame(
            board = newBoard,
            initialTurn = turn.opposite()
        )
    }

    /**
     * Resets the game to its initial state.
     *
     * Clears the board and sets the turn back to Player X.
     */
    fun reset() {
        board.reset()
        turn = PlayerMark.X
    }

    /**
     * Creates an immutable snapshot of the current game state.
     *
     * @return A [GameState] object representing the current state
     */
    fun getState(): GameState = GameState(
        board = board.createSnapshot(),
        nextToPlay = turn,
        isOver = isOver,
        isDraw = isDraw,
        winner = winner,
    )
}
