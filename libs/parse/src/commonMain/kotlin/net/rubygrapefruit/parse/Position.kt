package net.rubygrapefruit.parse

import kotlin.jvm.JvmInline

/**
 * An absolute position in the input stream.
 */
@JvmInline
value class Position(val value: Int) {
    operator fun plus(count: Int): Position {
        return Position(value + count)
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
