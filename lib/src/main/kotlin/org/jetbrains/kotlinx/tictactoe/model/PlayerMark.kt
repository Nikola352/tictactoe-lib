package org.jetbrains.kotlinx.tictactoe.model

/**
 * Represents a player's mark in the game.
 * 
 * There are two possible marks: [X] and [O].
 */
enum class PlayerMark {
    /** The X player mark */
    X,
    /** The O player mark */
    O,
}

/**
 * Returns the opposite player mark.
 * 
 * @return [PlayerMark.O] if this is [PlayerMark.X], [PlayerMark.X] if this is [PlayerMark.O]
 */
fun PlayerMark.opposite(): PlayerMark = when (this) {
    PlayerMark.X -> PlayerMark.O
    PlayerMark.O -> PlayerMark.X
}
