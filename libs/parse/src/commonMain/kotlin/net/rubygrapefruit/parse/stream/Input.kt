package net.rubygrapefruit.parse.stream

import net.rubygrapefruit.parse.Position

/**
 * A read-only mutable stream of input values.
 */
internal interface Input<POS> {
    val available: Int

    val finished: Boolean

    val position: Position
}