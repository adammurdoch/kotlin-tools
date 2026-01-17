package net.rubygrapefruit.parse.text

internal class StringCharStream(val text: String) : AdvancingCharStream {
    private var pos = 0

    override val available: Int
        get() = text.length - pos

    override val finished: Boolean
        get() = true

    override fun get(index: Int): Char {
        return text[index + pos]
    }

    override fun get(start: Int, end: Int): String {
        return text.substring(start + pos, end + pos)
    }

    override fun advance(count: Int) {
        pos += count
    }

    override fun posAt(index: Int): CharPosition {
        val offset = index + pos
        return text.posAt(CharPosition(0, 1, 1), offset)
    }
}