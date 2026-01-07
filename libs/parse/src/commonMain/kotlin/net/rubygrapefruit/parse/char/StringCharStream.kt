package net.rubygrapefruit.parse.char

internal class StringCharStream(val text: String) : AdvancingCharStream {
    private var pos = 0

    override val available: Int
        get() = text.length - pos

    override val finished: Boolean
        get() = true

    override fun get(index: Int): Char {
        return text[index + pos]
    }

    override fun advance(count: Int) {
        pos += count
    }

    override fun posAt(index: Int): CharPosition {
        val offset = index + pos
        return CharPosition(offset, 1, offset + 1)
    }
}