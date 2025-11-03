package org.jetbrains.kotlinx.tictactoe.internal

import org.jetbrains.kotlinx.tictactoe.model.PlayerMark
import kotlin.test.*

class MarkCounterTest {

    @Test
    fun `new counter should have all counts at zero`() {
        val counter = MarkCounter(3)

        for (i in 0 until 3) {
            assertEquals(0, counter.getCount(i, PlayerMark.X))
            assertEquals(0, counter.getCount(i, PlayerMark.O))
        }
    }

    @Test
    fun `should increment X count at specific index`() {
        val counter = MarkCounter(3)

        counter.increaseCount(0, PlayerMark.X)

        assertEquals(1, counter.getCount(0, PlayerMark.X))
        assertEquals(0, counter.getCount(1, PlayerMark.X))
        assertEquals(0, counter.getCount(2, PlayerMark.X))
    }

    @Test
    fun `should increment O count at specific index`() {
        val counter = MarkCounter(3)

        counter.increaseCount(1, PlayerMark.O)

        assertEquals(1, counter.getCount(1, PlayerMark.O))
        assertEquals(0, counter.getCount(0, PlayerMark.O))
        assertEquals(0, counter.getCount(2, PlayerMark.O))
    }

    @Test
    fun `should increment counts multiple times at same index`() {
        val counter = MarkCounter(3)

        counter.increaseCount(0, PlayerMark.X)
        counter.increaseCount(0, PlayerMark.X)
        counter.increaseCount(0, PlayerMark.X)

        assertEquals(3, counter.getCount(0, PlayerMark.X))
    }

    @Test
    fun `should track X and O counts independently`() {
        val counter = MarkCounter(3)

        counter.increaseCount(0, PlayerMark.X)
        counter.increaseCount(0, PlayerMark.X)
        counter.increaseCount(0, PlayerMark.O)
        counter.increaseCount(0, PlayerMark.O)
        counter.increaseCount(0, PlayerMark.O)

        assertEquals(2, counter.getCount(0, PlayerMark.X))
        assertEquals(3, counter.getCount(0, PlayerMark.O))
    }

    @Test
    fun `should track counts at different indices independently`() {
        val counter = MarkCounter(3)

        counter.increaseCount(0, PlayerMark.X)
        counter.increaseCount(1, PlayerMark.X)
        counter.increaseCount(1, PlayerMark.X)
        counter.increaseCount(2, PlayerMark.O)
        counter.increaseCount(2, PlayerMark.O)
        counter.increaseCount(2, PlayerMark.O)

        assertEquals(1, counter.getCount(0, PlayerMark.X))
        assertEquals(2, counter.getCount(1, PlayerMark.X))
        assertEquals(0, counter.getCount(2, PlayerMark.X))
        assertEquals(0, counter.getCount(0, PlayerMark.O))
        assertEquals(0, counter.getCount(1, PlayerMark.O))
        assertEquals(3, counter.getCount(2, PlayerMark.O))
    }

    @Test
    fun `reset should clear all counts to zero`() {
        val counter = MarkCounter(3)

        counter.increaseCount(0, PlayerMark.X)
        counter.increaseCount(1, PlayerMark.O)
        counter.increaseCount(2, PlayerMark.X)
        counter.increaseCount(2, PlayerMark.O)

        counter.reset()

        for (i in 0 until 3) {
            assertEquals(0, counter.getCount(i, PlayerMark.X))
            assertEquals(0, counter.getCount(i, PlayerMark.O))
        }
    }

    @Test
    fun `should handle custom sizes`() {
        val counter = MarkCounter(5)

        for (i in 0 until 5) {
            counter.increaseCount(i, PlayerMark.X)
        }

        for (i in 0 until 5) {
            assertEquals(1, counter.getCount(i, PlayerMark.X))
        }
    }

    @Test
    fun `copyFrom should copy all counts from another counter`() {
        val source = MarkCounter(3)
        source.increaseCount(0, PlayerMark.X)
        source.increaseCount(0, PlayerMark.X)
        source.increaseCount(1, PlayerMark.O)
        source.increaseCount(2, PlayerMark.X)
        source.increaseCount(2, PlayerMark.O)
        source.increaseCount(2, PlayerMark.O)

        val destination = MarkCounter(3)
        destination.copyFrom(source)

        assertEquals(2, destination.getCount(0, PlayerMark.X))
        assertEquals(0, destination.getCount(0, PlayerMark.O))
        assertEquals(0, destination.getCount(1, PlayerMark.X))
        assertEquals(1, destination.getCount(1, PlayerMark.O))
        assertEquals(1, destination.getCount(2, PlayerMark.X))
        assertEquals(2, destination.getCount(2, PlayerMark.O))
    }

    @Test
    fun `copyFrom should overwrite existing counts`() {
        val source = MarkCounter(3)
        source.increaseCount(0, PlayerMark.X)
        source.increaseCount(1, PlayerMark.O)

        val destination = MarkCounter(3)
        destination.increaseCount(0, PlayerMark.O)
        destination.increaseCount(0, PlayerMark.O)
        destination.increaseCount(2, PlayerMark.X)

        destination.copyFrom(source)

        assertEquals(1, destination.getCount(0, PlayerMark.X))
        assertEquals(0, destination.getCount(0, PlayerMark.O))
        assertEquals(0, destination.getCount(1, PlayerMark.X))
        assertEquals(1, destination.getCount(1, PlayerMark.O))
        assertEquals(0, destination.getCount(2, PlayerMark.X))
    }

    @Test
    fun `copyFrom should create independent copy`() {
        val source = MarkCounter(3)
        source.increaseCount(0, PlayerMark.X)

        val destination = MarkCounter(3)
        destination.copyFrom(source)

        // Modify source after copy
        source.increaseCount(0, PlayerMark.X)
        source.increaseCount(1, PlayerMark.O)

        // Destination should not reflect changes
        assertEquals(1, destination.getCount(0, PlayerMark.X))
        assertEquals(0, destination.getCount(1, PlayerMark.O))

        // Source should have the new values
        assertEquals(2, source.getCount(0, PlayerMark.X))
        assertEquals(1, source.getCount(1, PlayerMark.O))
    }

    @Test
    fun `copyFrom should throw exception for different sizes`() {
        val source = MarkCounter(3)
        val destination = MarkCounter(4)

        val exception = assertFailsWith<IllegalArgumentException> {
            destination.copyFrom(source)
        }

        assertTrue(exception.message!!.contains("different size", ignoreCase = true))
    }

    @Test
    fun `copyFrom should work with empty counter`() {
        val source = MarkCounter(3)
        val destination = MarkCounter(3)

        destination.increaseCount(0, PlayerMark.X)
        destination.increaseCount(1, PlayerMark.O)

        destination.copyFrom(source)

        for (i in 0 until 3) {
            assertEquals(0, destination.getCount(i, PlayerMark.X))
            assertEquals(0, destination.getCount(i, PlayerMark.O))
        }
    }

    @Test
    fun `should handle large counts`() {
        val counter = MarkCounter(1)

        repeat(100) {
            counter.increaseCount(0, PlayerMark.X)
        }

        assertEquals(100, counter.getCount(0, PlayerMark.X))
    }

    @Test
    fun `reset should be reusable`() {
        val counter = MarkCounter(3)

        counter.increaseCount(0, PlayerMark.X)
        counter.reset()
        counter.increaseCount(0, PlayerMark.X)
        counter.increaseCount(0, PlayerMark.X)

        assertEquals(2, counter.getCount(0, PlayerMark.X))
    }

    @Test
    fun `copyFrom with same instance should work`() {
        val counter = MarkCounter(3)
        counter.increaseCount(0, PlayerMark.X)
        counter.increaseCount(1, PlayerMark.O)

        // Copy to itself should work without issues
        counter.copyFrom(counter)

        assertEquals(1, counter.getCount(0, PlayerMark.X))
        assertEquals(1, counter.getCount(1, PlayerMark.O))
    }
}
