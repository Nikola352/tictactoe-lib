package org.jetbrains.kotlinx.tictactoe.game

import kotlinx.coroutines.test.runTest
import org.jetbrains.kotlinx.tictactoe.api.GameEvent
import org.jetbrains.kotlinx.tictactoe.api.GameEventListener
import org.jetbrains.kotlinx.tictactoe.api.Player
import org.jetbrains.kotlinx.tictactoe.model.BoardCell
import org.jetbrains.kotlinx.tictactoe.model.BoardPosition
import org.jetbrains.kotlinx.tictactoe.model.GameState
import org.jetbrains.kotlinx.tictactoe.model.PlayerMark
import kotlin.test.*

class TicTacToeGameRunnerTest {

    private class TestPlayer(
        override val name: String,
        private val moves: List<BoardPosition>
    ) : Player {
        private var moveIndex = 0

        override suspend fun selectMove(gameState: GameState): BoardPosition {
            return if (moveIndex < moves.size) moves[moveIndex++]
            else gameState.getAvailableMoves().random()
        }
    }

    private class EventCapturingListener : GameEventListener {
        val events = mutableListOf<GameEvent>()

        override suspend fun onGameEvent(event: GameEvent) {
            events.add(event)
        }
    }

    @Test
    fun `should run complete game with X winning`() = runTest {
        val xMoves = listOf(
            BoardPosition(0, 0),
            BoardPosition(0, 1),
            BoardPosition(0, 2)
        )
        val oMoves = listOf(
            BoardPosition(1, 0),
            BoardPosition(1, 1)
        )

        val listener = EventCapturingListener()
        val runner = TicTacToeGameRunner(
            xPlayerProvider = { TestPlayer("X", xMoves) },
            oPlayerProvider = { TestPlayer("O", oMoves) },
            listener = listener,
            boardSize = 3
        )

        runner.play()

        // Should have: initial board + 5 moves + game over
        assertEquals(7, listener.events.size)

        val lastEvent = listener.events.last()
        assertTrue(lastEvent is GameEvent.GameOver)
        assertEquals(PlayerMark.X, lastEvent.winner)
        assertFalse(lastEvent.isDraw)
    }

    @Test
    fun `should run complete game with draw`() = runTest {
        val xMoves = listOf(
            BoardPosition(0, 0),
            BoardPosition(0, 2),
            BoardPosition(1, 2),
            BoardPosition(2, 1),
            BoardPosition(2, 2)
        )
        val oMoves = listOf(
            BoardPosition(0, 1),
            BoardPosition(1, 0),
            BoardPosition(1, 1),
            BoardPosition(2, 0)
        )

        val listener = EventCapturingListener()
        val runner = TicTacToeGameRunner(
            xPlayerProvider = { TestPlayer("X", xMoves) },
            oPlayerProvider = { TestPlayer("O", oMoves) },
            listener = listener,
            boardSize = 3
        )

        runner.play()

        val lastEvent = listener.events.last()
        assertTrue(lastEvent is GameEvent.GameOver)
        assertNull(lastEvent.winner)
        assertTrue(lastEvent.isDraw)
    }

    @Test
    fun `should emit BoardUpdated events after each move`() = runTest {
        val xMoves = listOf(BoardPosition(0, 0), BoardPosition(0, 1))
        val oMoves = listOf(BoardPosition(1, 1), BoardPosition(2, 2))

        val listener = EventCapturingListener()
        val runner = TicTacToeGameRunner(
            xPlayerProvider = { TestPlayer("X", xMoves) },
            oPlayerProvider = { TestPlayer("O", oMoves) },
            listener = listener,
            boardSize = 3
        )

        runner.play()

        val boardUpdateEvents = listener.events.filterIsInstance<GameEvent.BoardUpdated>()
        assertTrue(boardUpdateEvents.size >= 5) // Initial + 4 moves

        // Verify board states are updated
        val secondMove = boardUpdateEvents[2].state
        assertEquals(BoardCell.Occupied(PlayerMark.X), secondMove.board[0, 0])
        assertEquals(BoardCell.Occupied(PlayerMark.O), secondMove.board[1, 1])
    }

    @Test
    fun `should handle invalid moves and retry`() = runTest {
        val xMoves = listOf(
            BoardPosition(0, 0),
            BoardPosition(0, 0), // Invalid - already occupied
            BoardPosition(0, 1)  // Retry with valid move
        )
        val oMoves = listOf(BoardPosition(1, 1))

        val listener = EventCapturingListener()
        val runner = TicTacToeGameRunner(
            xPlayerProvider = { TestPlayer("X", xMoves) },
            oPlayerProvider = { TestPlayer("O", oMoves) },
            listener = listener,
            boardSize = 3
        )

        runner.play()

        val invalidMoveEvents = listener.events.filterIsInstance<GameEvent.InvalidMove>()
        assertEquals(1, invalidMoveEvents.size)
        assertTrue(invalidMoveEvents[0].message.contains("taken", ignoreCase = true))
    }

    @Test
    fun `should create new player instances for each game`() = runTest {
        var xCreationCount = 0
        var oCreationCount = 0

        val listener = EventCapturingListener()
        val runner = TicTacToeGameRunner(
            xPlayerProvider = {
                xCreationCount++
                TestPlayer("X", listOf(BoardPosition(0, 0), BoardPosition(0, 1), BoardPosition(0, 2)))
            },
            oPlayerProvider = {
                oCreationCount++
                TestPlayer("O", listOf(BoardPosition(1, 0), BoardPosition(1, 1)))
            },
            listener = listener,
            boardSize = 3
        )

        runner.play()

        assertEquals(1, xCreationCount)
        assertEquals(1, oCreationCount)
    }

    @Test
    fun `should emit game over event with correct winner`() = runTest {
        val xMoves = listOf(
            BoardPosition(0, 0),
            BoardPosition(1, 1),
            BoardPosition(2, 2)
        )
        val oMoves = listOf(
            BoardPosition(0, 1),
            BoardPosition(0, 2)
        )

        val listener = EventCapturingListener()
        val runner = TicTacToeGameRunner(
            xPlayerProvider = { TestPlayer("X", xMoves) },
            oPlayerProvider = { TestPlayer("O", oMoves) },
            listener = listener,
            boardSize = 3
        )

        runner.play()

        val gameOverEvent = listener.events.filterIsInstance<GameEvent.GameOver>().first()
        assertEquals(PlayerMark.X, gameOverEvent.winner)
        assertFalse(gameOverEvent.isDraw)
    }

    @Test
    fun `should support custom board sizes`() = runTest {
        val xMoves = listOf(
            BoardPosition(0, 0),
            BoardPosition(0, 1),
            BoardPosition(0, 2),
            BoardPosition(0, 3)
        )
        val oMoves = listOf(
            BoardPosition(1, 0),
            BoardPosition(1, 1),
            BoardPosition(1, 2)
        )

        val listener = EventCapturingListener()
        val runner = TicTacToeGameRunner(
            xPlayerProvider = { TestPlayer("X", xMoves) },
            oPlayerProvider = { TestPlayer("O", oMoves) },
            listener = listener,
            boardSize = 4
        )

        runner.play()

        val boardUpdateEvent = listener.events.filterIsInstance<GameEvent.BoardUpdated>().first()
        assertEquals(4, boardUpdateEvent.state.board.size)
    }

    @Test
    fun `should provide correct game state in BoardUpdated events`() = runTest {
        val xMoves = listOf(BoardPosition(0, 0), BoardPosition(1, 1))
        val oMoves = listOf(BoardPosition(0, 1))

        val listener = EventCapturingListener()
        val runner = TicTacToeGameRunner(
            xPlayerProvider = { TestPlayer("X", xMoves) },
            oPlayerProvider = { TestPlayer("O", oMoves) },
            listener = listener,
            boardSize = 3
        )

        runner.play()

        val boardUpdates = listener.events.filterIsInstance<GameEvent.BoardUpdated>()

        // After first move (X at 0,0)
        val afterFirstMove = boardUpdates[1].state
        assertEquals(PlayerMark.O, afterFirstMove.nextToPlay)
        assertEquals(BoardCell.Occupied(PlayerMark.X), afterFirstMove.board[0, 0])

        // After second move (O at 0,1)
        val afterSecondMove = boardUpdates[2].state
        assertEquals(PlayerMark.X, afterSecondMove.nextToPlay)
        assertEquals(BoardCell.Occupied(PlayerMark.O), afterSecondMove.board[0, 1])
    }

    @Test
    fun `should not emit BoardUpdated after invalid move`() = runTest {
        val xMoves = listOf(
            BoardPosition(0, 0),
            BoardPosition(0, 0), // Invalid
            BoardPosition(1, 1)  // Valid
        )
        val oMoves = listOf(BoardPosition(0, 1))

        val listener = EventCapturingListener()
        val runner = TicTacToeGameRunner(
            xPlayerProvider = { TestPlayer("X", xMoves) },
            oPlayerProvider = { TestPlayer("O", oMoves) },
            listener = listener,
            boardSize = 3
        )

        runner.play()

        // Second X move should be InvalidMove, not BoardUpdated
        val nextEvent = listener.events[3]
        assertTrue(nextEvent is GameEvent.InvalidMove)
    }
}
