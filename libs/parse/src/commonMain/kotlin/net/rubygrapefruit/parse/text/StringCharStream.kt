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
        return text.posAt(offset)
    }

    override fun contextAt(index: Int): CharFailureContext {
        val offset = index + pos
        val pos = text.posAt(offset)
        val lineText = text.line(offset)
        return AdvancingCharStream.CharStreamContext(pos, lineText)
    }

    private fun CharSequence.posAt(index: Int): CharPosition {
        var line = 1
        var col = 1

        for (i in 0 until index) {
            if (get(i) == '\n') {
                line++
                col = 1
            } else {
                col++
            }
        }

        return CharPosition(index, line, col)
    }

    private fun CharSequence.line(index: Int): String {
        if (index == 0 && isEmpty()) {
            return ""
        }
        val start = startLine(index)
        val end = endLine(index)
        return text.substring(start, end)
    }

    private fun CharSequence.startLine(index: Int): Int {
        for (i in index downTo 1) {
            if (get(i - 1) == '\n') {
                return i
            }
        }
        return 0
    }

    private fun CharSequence.endLine(index: Int): Int {
        for (i in index until length) {
            if (get(i) == '\n') {
                return i
            }
        }
        return length

    }
}