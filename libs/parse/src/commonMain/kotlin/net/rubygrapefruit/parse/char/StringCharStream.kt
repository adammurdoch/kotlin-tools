package net.rubygrapefruit.parse.char

internal class StringCharStream(val text: String) : CharStream {
    override val available: Int
        get() = text.length

    override val finished: Boolean
        get() = true

    override fun get(index: Int): Char {
        return text[index]
    }

    override fun posAt(index: Int): CharPosition {
        return CharPosition(index, 1, index + 1)
    }
}