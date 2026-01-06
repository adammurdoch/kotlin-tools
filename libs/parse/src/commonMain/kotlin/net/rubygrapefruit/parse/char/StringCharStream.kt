package net.rubygrapefruit.parse.char

internal class StringCharStream(val text: String) : CharStream {
    override val length: Int
        get() = text.length

    override fun get(index: Int): Char {
        return text[index]
    }
}