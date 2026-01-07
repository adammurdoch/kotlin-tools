package net.rubygrapefruit.parse

internal interface Input<POS> {
    val available: Int

    val finished: Boolean

    fun posAt(index: Int): POS
}