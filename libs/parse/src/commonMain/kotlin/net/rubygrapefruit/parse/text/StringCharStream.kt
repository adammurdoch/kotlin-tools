package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Position
import net.rubygrapefruit.parse.minus
import net.rubygrapefruit.parse.plus

internal class StringCharStream(val text: String) : AdvancingCharStream {
    override var position = Position.Zero
        private set

    override val available: Int
        get() = text.length - position

    override val finished: Boolean
        get() = true

    override fun get(index: Int): Char {
        return text[index + position]
    }

    override fun get(start: Int, end: Int): String {
        return text.substring(start + position, end + position)
    }

    override fun advance(count: Int) {
        position += count
    }

    override fun contextAt(position: Position): TextFailureContext {
        val offset = position.value

        var line = 1
        var col = 1
        for (i in 0 until offset) {
            if (text[i] == '\n') {
                line++
                col = 1
            } else {
                col++
            }
        }

        val start = text.startLine(offset)
        var end = text.endLine(offset)
        if (end > 0 && end < text.length && text[end - 1] == '\r') {
            if (end == offset) {
                col--
            }
            end--
        }

        val pos = CharPosition(Position(offset), line, col)
        return AdvancingCharStream.TextStreamContext(pos, text.substring(start, end))
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