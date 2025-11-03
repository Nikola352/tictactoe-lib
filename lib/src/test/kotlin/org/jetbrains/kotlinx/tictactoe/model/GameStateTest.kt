package org.jetbrains.kotlinx.tictactoe.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameStateTest {

    private fun createEmptyBoard(size: Int = 3): BoardState {
        val cells = List(size) { List(size) { BoardCell.Empty } }
        return BoardState(cells)
    }

    private fun createBoardWithMoves(vararg moves: Pair<BoardPosition, PlayerMark>): BoardState {
        val cells: MutableList<MutableList<BoardCell>> = MutableList(3) { MutableList(3) { BoardCell.Empty } }
        moves.forEach { (position, mark) ->
            cells[position.row][position.column] = BoardCell.Occupied(mark)
        }
        return BoardState(cells.map { it.toList() })
    }

    @Test
    fun `isValidMove should return true for empty cell in active game`() {
        val board = createEmptyBoard()
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.X,
            isOver = false,
            isDraw = false,
            winner = null
        )

        assertTrue(gameState.isValidMove(BoardPosition(0, 0)))
        assertTrue(gameState.isValidMove(BoardPosition(1, 1)))
        assertTrue(gameState.isValidMove(BoardPosition(2, 2)))
    }

    @Test
    fun `isValidMove should return false for occupied cell`() {
        val board = createBoardWithMoves(
            BoardPosition(0, 0) to PlayerMark.X,
            BoardPosition(1, 1) to PlayerMark.O
        )
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.X,
            isOver = false,
            isDraw = false,
            winner = null
        )

        assertFalse(gameState.isValidMove(BoardPosition(0, 0)))
        assertFalse(gameState.isValidMove(BoardPosition(1, 1)))
        assertTrue(gameState.isValidMove(BoardPosition(2, 2)))
    }

    @Test
    fun `isValidMove should return false when game is over`() {
        val board = createBoardWithMoves(
            BoardPosition(0, 0) to PlayerMark.X,
            BoardPosition(0, 1) to PlayerMark.X,
            BoardPosition(0, 2) to PlayerMark.X
        )
        val gameState = GameState(
            board = board,
            nextToPlay = null,
            isOver = true,
            isDraw = false,
            winner = PlayerMark.X
        )

        assertFalse(gameState.isValidMove(BoardPosition(1, 0)))
        assertFalse(gameState.isValidMove(BoardPosition(1, 1)))
        assertFalse(gameState.isValidMove(BoardPosition(2, 2)))
    }

    @Test
    fun `isValidMove should return false for invalid position outside board`() {
        val board = createEmptyBoard(3)
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.X,
            isOver = false,
            isDraw = false,
            winner = null
        )

        assertFalse(gameState.isValidMove(BoardPosition(-1, 0)))
        assertFalse(gameState.isValidMove(BoardPosition(0, -1)))
        assertFalse(gameState.isValidMove(BoardPosition(3, 0)))
        assertFalse(gameState.isValidMove(BoardPosition(0, 3)))
        assertFalse(gameState.isValidMove(BoardPosition(5, 5)))
    }

    @Test
    fun `isValidMove should work with custom board size`() {
        val board = createEmptyBoard(4)
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.X,
            isOver = false,
            isDraw = false,
            winner = null
        )

        assertTrue(gameState.isValidMove(BoardPosition(3, 3)))
        assertFalse(gameState.isValidMove(BoardPosition(4, 0)))
        assertFalse(gameState.isValidMove(BoardPosition(0, 4)))
    }

    @Test
    fun `getAvailableMoves should return all positions for empty board`() {
        val board = createEmptyBoard()
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.X,
            isOver = false,
            isDraw = false,
            winner = null
        )

        val availableMoves = gameState.getAvailableMoves()

        assertEquals(9, availableMoves.size)
        assertTrue(availableMoves.contains(BoardPosition(0, 0)))
        assertTrue(availableMoves.contains(BoardPosition(1, 1)))
        assertTrue(availableMoves.contains(BoardPosition(2, 2)))
    }

    @Test
    fun `getAvailableMoves should exclude occupied positions`() {
        val board = createBoardWithMoves(
            BoardPosition(0, 0) to PlayerMark.X,
            BoardPosition(1, 1) to PlayerMark.O,
            BoardPosition(2, 2) to PlayerMark.X
        )
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.O,
            isOver = false,
            isDraw = false,
            winner = null
        )

        val availableMoves = gameState.getAvailableMoves()

        assertEquals(6, availableMoves.size)
        assertFalse(availableMoves.contains(BoardPosition(0, 0)))
        assertFalse(availableMoves.contains(BoardPosition(1, 1)))
        assertFalse(availableMoves.contains(BoardPosition(2, 2)))
        assertTrue(availableMoves.contains(BoardPosition(0, 1)))
        assertTrue(availableMoves.contains(BoardPosition(1, 0)))
    }

    @Test
    fun `getAvailableMoves should return empty list when game is over`() {
        val board = createBoardWithMoves(
            BoardPosition(0, 0) to PlayerMark.X,
            BoardPosition(0, 1) to PlayerMark.X,
            BoardPosition(0, 2) to PlayerMark.X
        )
        val gameState = GameState(
            board = board,
            nextToPlay = null,
            isOver = true,
            isDraw = false,
            winner = PlayerMark.X
        )

        val availableMoves = gameState.getAvailableMoves()

        assertTrue(availableMoves.isEmpty())
    }

    @Test
    fun `getAvailableMoves should return empty list for draw game`() {
        // Full board with no winner
        val board = createBoardWithMoves(
            BoardPosition(0, 0) to PlayerMark.X,
            BoardPosition(0, 1) to PlayerMark.O,
            BoardPosition(0, 2) to PlayerMark.X,
            BoardPosition(1, 0) to PlayerMark.O,
            BoardPosition(1, 1) to PlayerMark.O,
            BoardPosition(1, 2) to PlayerMark.X,
            BoardPosition(2, 0) to PlayerMark.O,
            BoardPosition(2, 1) to PlayerMark.X,
            BoardPosition(2, 2) to PlayerMark.O
        )
        val gameState = GameState(
            board = board,
            nextToPlay = null,
            isOver = true,
            isDraw = true,
            winner = null
        )

        val availableMoves = gameState.getAvailableMoves()

        assertTrue(availableMoves.isEmpty())
    }

    @Test
    fun `getAvailableMoves should return single position when only one cell empty`() {
        val board = createBoardWithMoves(
            BoardPosition(0, 0) to PlayerMark.X,
            BoardPosition(0, 1) to PlayerMark.O,
            BoardPosition(0, 2) to PlayerMark.X,
            BoardPosition(1, 0) to PlayerMark.O,
            BoardPosition(1, 1) to PlayerMark.X,
            BoardPosition(1, 2) to PlayerMark.O,
            BoardPosition(2, 0) to PlayerMark.X,
            BoardPosition(2, 1) to PlayerMark.O
            // 2,2 is empty
        )
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.X,
            isOver = false,
            isDraw = false,
            winner = null
        )

        val availableMoves = gameState.getAvailableMoves()

        assertEquals(1, availableMoves.size)
        assertEquals(BoardPosition(2, 2), availableMoves[0])
    }

    @Test
    fun `getAvailableMoves should return positions in consistent order`() {
        val board = createEmptyBoard()
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.X,
            isOver = false,
            isDraw = false,
            winner = null
        )

        val availableMoves = gameState.getAvailableMoves()

        // Should be ordered by row, then column
        assertEquals(BoardPosition(0, 0), availableMoves[0])
        assertEquals(BoardPosition(0, 1), availableMoves[1])
        assertEquals(BoardPosition(0, 2), availableMoves[2])
        assertEquals(BoardPosition(1, 0), availableMoves[3])
        assertEquals(BoardPosition(1, 1), availableMoves[4])
        assertEquals(BoardPosition(1, 2), availableMoves[5])
        assertEquals(BoardPosition(2, 0), availableMoves[6])
        assertEquals(BoardPosition(2, 1), availableMoves[7])
        assertEquals(BoardPosition(2, 2), availableMoves[8])
    }

    @Test
    fun `getAvailableMoves should work with custom board size`() {
        val board = createEmptyBoard(4)
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.X,
            isOver = false,
            isDraw = false,
            winner = null
        )

        val availableMoves = gameState.getAvailableMoves()

        assertEquals(16, availableMoves.size)
        assertTrue(availableMoves.contains(BoardPosition(0, 0)))
        assertTrue(availableMoves.contains(BoardPosition(3, 3)))
    }

    @Test
    fun `getAvailableMoves should handle partially filled board correctly`() {
        val board = createBoardWithMoves(
            BoardPosition(0, 0) to PlayerMark.X,
            BoardPosition(2, 2) to PlayerMark.O
        )
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.X,
            isOver = false,
            isDraw = false,
            winner = null
        )

        val availableMoves = gameState.getAvailableMoves()

        assertEquals(7, availableMoves.size)
        assertFalse(availableMoves.contains(BoardPosition(0, 0)))
        assertFalse(availableMoves.contains(BoardPosition(2, 2)))
        assertTrue(availableMoves.contains(BoardPosition(0, 1)))
        assertTrue(availableMoves.contains(BoardPosition(1, 1)))
    }

    @Test
    fun `isValidMove and getAvailableMoves should be consistent`() {
        val board = createBoardWithMoves(
            BoardPosition(0, 0) to PlayerMark.X,
            BoardPosition(1, 1) to PlayerMark.O
        )
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.X,
            isOver = false,
            isDraw = false,
            winner = null
        )

        val availableMoves = gameState.getAvailableMoves()

        // Every position in availableMoves should be valid
        availableMoves.forEach { position ->
            assertTrue(gameState.isValidMove(position), "Position $position should be valid")
        }

        // Every valid position should be in availableMoves
        for (row in 0 until board.size) {
            for (col in 0 until board.size) {
                val position = BoardPosition(row, col)
                if (gameState.isValidMove(position)) {
                    assertTrue(
                        availableMoves.contains(position),
                        "Valid position $position should be in available moves"
                    )
                }
            }
        }
    }

    @Test
    fun `isValidMove should handle corner cases`() {
        val board = createBoardWithMoves(
            BoardPosition(0, 0) to PlayerMark.X
        )
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.O,
            isOver = false,
            isDraw = false,
            winner = null
        )

        // Test all corners
        assertFalse(gameState.isValidMove(BoardPosition(0, 0))) // occupied
        assertTrue(gameState.isValidMove(BoardPosition(0, 2)))  // top-right
        assertTrue(gameState.isValidMove(BoardPosition(2, 0)))  // bottom-left
        assertTrue(gameState.isValidMove(BoardPosition(2, 2)))  // bottom-right
    }

    @Test
    fun `getAvailableMoves should handle board with moves in all rows`() {
        val board = createBoardWithMoves(
            BoardPosition(0, 1) to PlayerMark.X,
            BoardPosition(1, 1) to PlayerMark.O,
            BoardPosition(2, 1) to PlayerMark.X
        )
        val gameState = GameState(
            board = board,
            nextToPlay = PlayerMark.O,
            isOver = false,
            isDraw = false,
            winner = null
        )

        val availableMoves = gameState.getAvailableMoves()

        assertEquals(6, availableMoves.size)
        // Each row should have 2 available positions (columns 0 and 2)
        assertTrue(availableMoves.contains(BoardPosition(0, 0)))
        assertTrue(availableMoves.contains(BoardPosition(0, 2)))
        assertTrue(availableMoves.contains(BoardPosition(1, 0)))
        assertTrue(availableMoves.contains(BoardPosition(1, 2)))
        assertTrue(availableMoves.contains(BoardPosition(2, 0)))
        assertTrue(availableMoves.contains(BoardPosition(2, 2)))
    }
}
