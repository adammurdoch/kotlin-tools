package net.rubygrapefruit.parse

import kotlin.jvm.JvmInline

/**
 * An absolute position in the input stream.
 */
@JvmInline
value class Position(val value: Int): Comparable<Position> {
    override fun compareTo(other: Position): Int {
        return value.compareTo(other.value)
    }

    operator fun plus(count: Int): Position {
        return Position(value + count)
    }

    operator fun minus(other: Position): Int {
        return value - other.value
    }

    companion object {
        val Zero = Position(0)
    }
}

operator fun Int.minus(offset: Position): Int {
    return this - offset.value
}

operator fun Int.plus(offset: Position): Int {
    return this + offset.value
}
