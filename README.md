# Tic-Tac-Toe Library

A flexible, well-tested Kotlin library for building tic-tac-toe games with customizable players and UI implementations.

## Project Structure

```
├── lib/                    # Core library (game engine, DSL, models)
├── lib-players/           # Pre-built AI players (Random, Minimax)
├── app/                   # Command-line game demo
└── notebook/              # Kotlin notebook with usage examples
```

## Features

- **Flexible Architecture**: Clean separation between game logic and UI
- **Multiple APIs**: Direct engine access, game runner, or convenient DSL
- **Coroutine Support**: Async player moves and event handling
- **Extensible Player System**: Easy to implement custom AI or human players
- **Event-Driven**: React to game state changes through listeners
- **Configurable Board Size**: Play on any NxN board (default 3x3)
- **Immutable State**: Safe snapshots for analysis and serialization

## Quick Start

### Running the Demo

```bash
./gradlew :app:run
```

This launches an interactive command-line game where you can play against various AI opponents.

### Using the Library

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":lib"))
    implementation(project(":lib-players")) // Optional: pre-built AI players
}
```

### Basic Usage

**Using the DSL (Recommended):**

```kotlin
import org.jetbrains.kotlinx.tictactoe.dsl.*
import org.jetbrains.kotlinx.tictactoe.players.*

runBlocking {
    ticTacToeGame {
        playerX { state -> 
            // Your move selection logic
            state.getAvailableMoves().first()
        }
        
        playerO("AI Bot") { state ->
            RandomPlayer().selectMove(state)
        }
        
        onEvent { event ->
            when (event) {
                is GameEvent.BoardUpdated -> println("Board updated!")
                is GameEvent.GameOver -> println("Winner: ${event.winner}")
                else -> {}
            }
        }
    }.play()
}
```

**Using the Game Engine Directly:**

```kotlin
import org.jetbrains.kotlinx.tictactoe.game.*
import org.jetbrains.kotlinx.tictactoe.model.*

val game = TicTacToeGame(boardSize = 3)

game.playMove(BoardPosition(0, 0)) // X plays
game.playMove(BoardPosition(1, 1)) // O plays

val state = game.getState()
println("Next player: ${state.nextToPlay}")
println("Game over: ${state.isOver}")
```

## Creating Custom Players

Implement the `Player` interface:

```kotlin
class MyAI(override val name: String) : Player {
    override suspend fun selectMove(gameState: GameState): BoardPosition {
        // Your AI logic here
        return gameState.getAvailableMoves().random()
    }
}
```

## Exploring the Library

Check out the **Kotlin notebook** (`notebook/demo.ipynb`) for comprehensive examples including:
- Direct engine usage
- Creating AI players (Random, Smart, Minimax)
- Using the game runner
- DSL examples
- Custom board sizes

## Key Components

### Core Library (`lib/`)

- **`TicTacToeGame`**: Core game engine with move validation and win detection
- **`TicTacToeGameRunner`**: Orchestrates games with automatic turn management
- **`Player`**: Interface for implementing human or AI players
- **`GameEventListener`**: React to game events (moves, invalid moves, game over)
- **DSL**: Type-safe builder for quick game setup

### Player Library (`lib-players/`)

- **`RandomPlayer`**: Makes random valid moves
- **`MinimaxPlayer`**: Perfect-play AI using the minimax algorithm

### CLI Demo (`app/`)

Interactive command-line game with:
- Human vs Human
- Human vs AI
- AI vs AI
- Configurable player types

## Architecture Highlights

- **Clean API**: Separate concerns (engine, orchestration, UI)
- **Immutable State**: `GameState` and `BoardState` are snapshots
- **Extensible**: Easy to add new player types or display formats
- **Testable**: Well-tested with comprehensive unit tests
- **Modern Kotlin**: Coroutines, sealed interfaces, DSL builders

## License

MIT License - feel free to use this library in your projects!
