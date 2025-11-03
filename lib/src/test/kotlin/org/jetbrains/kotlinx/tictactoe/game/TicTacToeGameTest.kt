package org.jetbrains.kotlinx.tictactoe.game

import org.jetbrains.kotlinx.tictactoe.model.BoardCell
import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark
import kotlin.test.*

class TicTacToeGameTest {

    @Test
    fun `new game should start with X turn and empty board`() {
        val game = TicTacToeGame(3)

        assertEquals(PlayerMark.X, game.turn)
        assertFalse(game.isOver)
        assertNull(game.winner)
        assertFalse(game.isDraw)
    }

    @Test
    fun `should switch turns after each move`() {
        val game = TicTacToeGame(3)

        assertEquals(PlayerMark.X, game.turn)

        game.playMove(BoardPosition(0, 0))
        assertEquals(PlayerMark.O, game.turn)

        game.playMove(BoardPosition(1, 1))
        assertEquals(PlayerMark.X, game.turn)
    }

    @Test
    fun `should detect X winning`() {
        val game = TicTacToeGame(3)

        game.playMove(BoardPosition(0, 0)) // X
        game.playMove(BoardPosition(1, 0)) // O
        game.playMove(BoardPosition(0, 1)) // X
        game.playMove(BoardPosition(1, 1)) // O
        game.playMove(BoardPosition(0, 2)) // X wins

        assertTrue(game.isOver)
        assertEquals(PlayerMark.X, game.winner)
        assertFalse(game.isDraw)
    }

    @Test
    fun `should detect O winning`() {
        val game = TicTacToeGame(3)

        game.playMove(BoardPosition(0, 0)) // X
        game.playMove(BoardPosition(1, 0)) // O
        game.playMove(BoardPosition(0, 1)) // X
        game.playMove(BoardPosition(1, 1)) // O
        game.playMove(BoardPosition(2, 2)) // X
        game.playMove(BoardPosition(1, 2)) // O wins

        assertTrue(game.isOver)
        assertEquals(PlayerMark.O, game.winner)
        assertFalse(game.isDraw)
    }

    @Test
    fun `should detect draw`() {
        val game = TicTacToeGame(3)

        // X O X
        // O O X
        // O X O
        game.playMove(BoardPosition(0, 0)) // X
        game.playMove(BoardPosition(0, 1)) // O
        game.playMove(BoardPosition(0, 2)) // X
        game.playMove(BoardPosition(1, 0)) // O
        game.playMove(BoardPosition(1, 2)) // X
        game.playMove(BoardPosition(1, 1)) // O
        game.playMove(BoardPosition(2, 1)) // X
        game.playMove(BoardPosition(2, 0)) // O
        game.playMove(BoardPosition(2, 2)) // X

        assertTrue(game.isOver)
        assertNull(game.winner)
        assertTrue(game.isDraw)
    }

    @Test
    fun `should throw exception when playing on occupied cell`() {
        val game = TicTacToeGame(3)

        game.playMove(BoardPosition(0, 0))

        assertFailsWith<IllegalArgumentException> {
            game.playMove(BoardPosition(0, 0))
        }
    }

    @Test
    fun `reset should clear board and return to initial state`() {
        val game = TicTacToeGame(3)

        game.playMove(BoardPosition(0, 0))
        game.playMove(BoardPosition(1, 1))
        game.playMove(BoardPosition(2, 2))

        game.reset()

        assertEquals(PlayerMark.X, game.turn)
        assertFalse(game.isOver)
        assertNull(game.winner)
        assertFalse(game.isDraw)

        val state = game.getState()
        state.board.forEach { _, _, cell ->
            assertEquals(BoardCell.Empty, cell)
        }
    }

    @Test
    fun `getState should return accurate game state`() {
        val game = TicTacToeGame(3)

        game.playMove(BoardPosition(0, 0))
        game.playMove(BoardPosition(1, 1))

        val state = game.getState()

        assertEquals(PlayerMark.X, state.nextToPlay)
        assertFalse(state.isOver)
        assertFalse(state.isDraw)
        assertNull(state.winner)
        assertEquals(BoardCell.Occupied(PlayerMark.X), state.board[0, 0])
        assertEquals(BoardCell.Occupied(PlayerMark.O), state.board[1, 1])
    }

    @Test
    fun `should create game from existing GameState`() {
        val originalGame = TicTacToeGame(3)

        originalGame.playMove(BoardPosition(0, 0))
        originalGame.playMove(BoardPosition(1, 1))
        originalGame.playMove(BoardPosition(0, 1))

        val state = originalGame.getState()
        val restoredGame = TicTacToeGame(state)

        assertEquals(originalGame.turn, restoredGame.turn)
        assertEquals(originalGame.isOver, restoredGame.isOver)
        assertEquals(originalGame.winner, restoredGame.winner)

        val restoredState = restoredGame.getState()
        assertEquals(state.board[0, 0], restoredState.board[0, 0])
        assertEquals(state.board[1, 1], restoredState.board[1, 1])
        assertEquals(state.board[0, 1], restoredState.board[0, 1])
    }

    @Test
    fun `withMove should not modify original game`() {
        val game = TicTacToeGame(3)

        game.playMove(BoardPosition(0, 0))
        val originalTurn = game.turn

        val newGame = game.withMove(BoardPosition(1, 1))

        // Original game unchanged
        assertEquals(originalTurn, game.turn)
        assertEquals(BoardCell.Empty, game.getState().board[1, 1])

        // New game has the move
        assertEquals(PlayerMark.X, newGame.turn)
        assertEquals(BoardCell.Occupied(PlayerMark.O), newGame.getState().board[1, 1])
    }

    @Test
    fun `withMove should create independent game copy`() {
        val game = TicTacToeGame(3)

        game.playMove(BoardPosition(0, 0))
        val newGame = game.withMove(BoardPosition(1, 1))

        // Continue original game
        game.playMove(BoardPosition(2, 2))

        // New game should not have this move
        assertEquals(BoardCell.Empty, newGame.getState().board[2, 2])
        assertEquals(BoardCell.Occupied(PlayerMark.O), game.getState().board[2, 2])
    }

    @Test
    fun `withMove should handle winning move correctly`() {
        val game = TicTacToeGame(3)

        game.playMove(BoardPosition(0, 0)) // X
        game.playMove(BoardPosition(1, 0)) // O
        game.playMove(BoardPosition(0, 1)) // X
        game.playMove(BoardPosition(1, 1)) // O

        val newGame = game.withMove(BoardPosition(0, 2)) // X wins

        assertTrue(newGame.isOver)
        assertEquals(PlayerMark.X, newGame.winner)

        // Original game should not be over
        assertFalse(game.isOver)
    }

    @Test
    fun `should handle custom board size`() {
        val game = TicTacToeGame(4)

        val state = game.getState()
        assertEquals(4, state.board.size)
    }
}
