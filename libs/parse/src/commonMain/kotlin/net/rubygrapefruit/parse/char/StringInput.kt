package net.rubygrapefruit.parse.char

internal class StringInput(val text: String) : CharInput {
    override val length: Int
        get() = text.length

    override fun next(index: Int): Char {
        return text[index]
    }
}