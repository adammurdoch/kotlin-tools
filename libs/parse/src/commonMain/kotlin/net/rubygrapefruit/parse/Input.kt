package net.rubygrapefruit.parse

/**
 * A read-only mutable stream of input values.
 */
internal interface Input<POS> {
    val available: Int

    val finished: Boolean

    val position: Position

    fun posAt(index: Int): POS
}