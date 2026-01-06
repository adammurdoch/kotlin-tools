package net.rubygrapefruit.parse.char

internal class StringCharStream(val text: String) : CharStream {
    override val length: Int
        get() = text.length

    override fun get(index: Int): Char {
        return text[index]
    }

    override fun posAt(index: Int): CharPosition {
        return CharPosition(index, 1, index + 1)
    }
}