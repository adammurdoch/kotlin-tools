package net.rubygrapefruit.parse

internal interface Input<POS> {
    val length: Int

    fun posAt(index: Int): POS
}