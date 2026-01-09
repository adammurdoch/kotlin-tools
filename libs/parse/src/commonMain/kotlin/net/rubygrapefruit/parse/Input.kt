package net.rubygrapefruit.parse

internal interface Input<POS> {
    val available: Int

    val finished: Boolean

    fun posAt(index: Int): POS

    fun mayHave(count: Int): Boolean {
        return count <= available || !finished
    }
}