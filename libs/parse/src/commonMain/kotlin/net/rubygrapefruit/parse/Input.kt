package net.rubygrapefruit.parse

/**
 * A read-only mutable stream of input values.
 */
internal interface Input<POS> {
    val available: Int

    val finished: Boolean

    val offset: Offset

    fun posAt(index: Int): POS
}