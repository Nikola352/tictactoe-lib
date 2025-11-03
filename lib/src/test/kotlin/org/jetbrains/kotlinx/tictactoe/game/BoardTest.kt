package org.jetbrains.kotlinx.tictactoe.game

import org.jetbrains.kotlinx.tictactoe.model.BoardCell
import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.BoardState
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark
import kotlin.test.*

class BoardTest {

    @Test
    fun `new board should be empty`() {
        val board = Board(3)
        val snapshot = board.createSnapshot()

        snapshot.forEach { row, col, cell ->
            assertEquals(BoardCell.Empty, cell, "Cell at ($row, $col) should be empty")
        }
        assertNull(board.winner)
        assertFalse(board.isOver)
    }

    @Test
    fun `should place mark on empty cell`() {
        val board = Board(3)
        val position = BoardPosition(1, 1)

        board.place(position, PlayerMark.X)
        val snapshot = board.createSnapshot()

        assertEquals(BoardCell.Occupied(PlayerMark.X), snapshot[position])
    }

    @Test
    fun `should throw exception when placing on occupied cell`() {
        val board = Board(3)
        val position = BoardPosition(0, 0)

        board.place(position, PlayerMark.X)

        assertFailsWith<IllegalArgumentException> {
            board.place(position, PlayerMark.O)
        }
    }

    @Test
    fun `should detect horizontal win`() {
        val board = Board(3)

        // X wins with top row
        board.place(BoardPosition(0, 0), PlayerMark.X)
        board.place(BoardPosition(0, 1), PlayerMark.X)
        board.place(BoardPosition(0, 2), PlayerMark.X)

        assertEquals(PlayerMark.X, board.winner)
        assertTrue(board.isOver)
    }

    @Test
    fun `should detect vertical win`() {
        val board = Board(3)

        // O wins with middle column
        board.place(BoardPosition(0, 1), PlayerMark.O)
        board.place(BoardPosition(1, 1), PlayerMark.O)
        board.place(BoardPosition(2, 1), PlayerMark.O)

        assertEquals(PlayerMark.O, board.winner)
        assertTrue(board.isOver)
    }

    @Test
    fun `should detect main diagonal win`() {
        val board = Board(3)

        // X wins with main diagonal (top-left to bottom-right)
        board.place(BoardPosition(0, 0), PlayerMark.X)
        board.place(BoardPosition(1, 1), PlayerMark.X)
        board.place(BoardPosition(2, 2), PlayerMark.X)

        assertEquals(PlayerMark.X, board.winner)
        assertTrue(board.isOver)
    }

    @Test
    fun `should detect minor diagonal win`() {
        val board = Board(3)

        // O wins with minor diagonal (top-right to bottom-left)
        board.place(BoardPosition(0, 2), PlayerMark.O)
        board.place(BoardPosition(1, 1), PlayerMark.O)
        board.place(BoardPosition(2, 0), PlayerMark.O)

        assertEquals(PlayerMark.O, board.winner)
        assertTrue(board.isOver)
    }

    @Test
    fun `should detect draw when board is full with no winner`() {
        val board = Board(3)

        // Create a draw scenario:
        // X O X
        // O O X
        // O X O
        board.place(BoardPosition(0, 0), PlayerMark.X)
        board.place(BoardPosition(0, 1), PlayerMark.O)
        board.place(BoardPosition(0, 2), PlayerMark.X)
        board.place(BoardPosition(1, 0), PlayerMark.O)
        board.place(BoardPosition(1, 1), PlayerMark.O)
        board.place(BoardPosition(1, 2), PlayerMark.X)
        board.place(BoardPosition(2, 0), PlayerMark.O)
        board.place(BoardPosition(2, 1), PlayerMark.X)
        board.place(BoardPosition(2, 2), PlayerMark.O)

        assertNull(board.winner)
        assertTrue(board.isOver)
    }

    @Test
    fun `should reset board to initial state`() {
        val board = Board(3)

        // Play some moves
        board.place(BoardPosition(0, 0), PlayerMark.X)
        board.place(BoardPosition(1, 1), PlayerMark.O)
        board.place(BoardPosition(2, 2), PlayerMark.X)

        board.reset()

        val snapshot = board.createSnapshot()
        snapshot.forEach { _, _, cell ->
            assertEquals(BoardCell.Empty, cell)
        }
        assertNull(board.winner)
        assertFalse(board.isOver)
    }

    @Test
    fun `should create accurate snapshot`() {
        val board = Board(3)

        board.place(BoardPosition(0, 0), PlayerMark.X)
        board.place(BoardPosition(1, 1), PlayerMark.O)

        val snapshot = board.createSnapshot()

        assertEquals(BoardCell.Occupied(PlayerMark.X), snapshot[0, 0])
        assertEquals(BoardCell.Occupied(PlayerMark.O), snapshot[1, 1])
        assertEquals(BoardCell.Empty, snapshot[2, 2])
    }

    @Test
    fun `should restore board from valid BoardState`() {
        val originalBoard = Board(3)

        // Create a specific game state
        originalBoard.place(BoardPosition(0, 0), PlayerMark.X)
        originalBoard.place(BoardPosition(1, 1), PlayerMark.O)
        originalBoard.place(BoardPosition(2, 2), PlayerMark.X)

        val snapshot = originalBoard.createSnapshot()
        val restoredBoard = Board(snapshot)

        assertEquals(originalBoard.isOver, restoredBoard.isOver)
        assertEquals(originalBoard.winner, restoredBoard.winner)

        val restoredSnapshot = restoredBoard.createSnapshot()
        assertEquals(BoardCell.Occupied(PlayerMark.X), restoredSnapshot[0, 0])
        assertEquals(BoardCell.Occupied(PlayerMark.O), restoredSnapshot[1, 1])
        assertEquals(BoardCell.Occupied(PlayerMark.X), restoredSnapshot[2, 2])
    }

    @Test
    fun `should restore board with winner state`() {
        val originalBoard = Board(3)

        // Create a winning state for X
        originalBoard.place(BoardPosition(0, 0), PlayerMark.X)
        originalBoard.place(BoardPosition(0, 1), PlayerMark.X)
        originalBoard.place(BoardPosition(0, 2), PlayerMark.X)

        val snapshot = originalBoard.createSnapshot()
        val restoredBoard = Board(snapshot)

        assertEquals(PlayerMark.X, restoredBoard.winner)
        assertTrue(restoredBoard.isOver)
    }

    @Test
    fun `should throw exception for invalid board state with two winners`() {
        // Manually create an invalid board state with both players winning
        val invalidCells = listOf(
            listOf(
                BoardCell.Occupied(PlayerMark.X),
                BoardCell.Occupied(PlayerMark.X),
                BoardCell.Occupied(PlayerMark.X)
            ),
            listOf(
                BoardCell.Occupied(PlayerMark.O),
                BoardCell.Occupied(PlayerMark.O),
                BoardCell.Occupied(PlayerMark.O)
            ),
            listOf(BoardCell.Empty, BoardCell.Empty, BoardCell.Empty)
        )
        val invalidState = BoardState(invalidCells)

        assertFailsWith<IllegalArgumentException> {
            Board(invalidState)
        }
    }

    @Test
    fun `should copy board with identical state`() {
        val board = Board(3)

        // Create a specific state
        board.place(BoardPosition(0, 0), PlayerMark.X)
        board.place(BoardPosition(1, 1), PlayerMark.O)
        board.place(BoardPosition(0, 1), PlayerMark.X)

        val copiedBoard = board.copy()

        // Verify copied state matches original
        assertEquals(board.winner, copiedBoard.winner)
        assertEquals(board.isOver, copiedBoard.isOver)

        val originalSnapshot = board.createSnapshot()
        val copiedSnapshot = copiedBoard.createSnapshot()

        originalSnapshot.forEach { row, col, cell ->
            assertEquals(cell, copiedSnapshot[row, col])
        }
    }

    @Test
    fun `copied board should be independent from original`() {
        val board = Board(3)
        board.place(BoardPosition(0, 0), PlayerMark.X)

        val copiedBoard = board.copy()

        // Modify original
        board.place(BoardPosition(1, 1), PlayerMark.O)

        // Copied board should not reflect the change
        val copiedSnapshot = copiedBoard.createSnapshot()
        assertEquals(BoardCell.Empty, copiedSnapshot[1, 1])
        assertEquals(BoardCell.Occupied(PlayerMark.X), copiedSnapshot[0, 0])
    }

    @Test
    fun `should handle custom board size`() {
        val board = Board(4)
        val snapshot = board.createSnapshot()

        assertEquals(4, snapshot.size)

        // Test win condition on 4x4 board
        board.place(BoardPosition(0, 0), PlayerMark.X)
        board.place(BoardPosition(0, 1), PlayerMark.X)
        board.place(BoardPosition(0, 2), PlayerMark.X)
        board.place(BoardPosition(0, 3), PlayerMark.X)

        assertEquals(PlayerMark.X, board.winner)
        assertTrue(board.isOver)
    }

    @Test
    fun `should not mark game as over until all cells filled or winner found`() {
        val board = Board(3)

        board.place(BoardPosition(0, 0), PlayerMark.X)
        assertFalse(board.isOver)

        board.place(BoardPosition(1, 1), PlayerMark.O)
        assertFalse(board.isOver)

        board.place(BoardPosition(2, 2), PlayerMark.X)
        assertFalse(board.isOver)
    }

    @Test
    fun `should detect win immediately when winning move is made`() {
        val board = Board(3)

        // Set up almost winning state
        board.place(BoardPosition(0, 0), PlayerMark.X)
        board.place(BoardPosition(0, 1), PlayerMark.X)
        assertFalse(board.isOver)
        assertNull(board.winner)

        // Complete the win
        board.place(BoardPosition(0, 2), PlayerMark.X)
        assertTrue(board.isOver)
        assertEquals(PlayerMark.X, board.winner)
    }
}
