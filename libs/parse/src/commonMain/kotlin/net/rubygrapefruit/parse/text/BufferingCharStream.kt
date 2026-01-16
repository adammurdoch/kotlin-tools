package net.rubygrapefruit.parse.text

internal class BufferingCharStream : AdvancingCharStream {
    private var tail = Buffer(null, 0)
    private var pos = 0
    override var finished: Boolean = false
        private set

    override val available: Int
        get() = tail.endIndex - pos

    override fun get(index: Int): Char {
        return tail.get(index + pos)
    }

    override fun get(start: Int, end: Int): String {
        return tail.get(start + pos, end + pos)
    }

    override fun posAt(index: Int): CharPosition {
        val offset = index + pos
        return CharPosition(offset, 1, offset + 1)
    }

    fun append(chars: CharArray) {
        tail = tail.append(chars)
    }

    override fun advance(count: Int) {
        pos += count
    }

    fun end() {
        finished = true
    }

    private class Buffer(private val previous: Buffer?, private val startIndex: Int) {
        private var writeIndex = 0
        private val content = CharArray(64 * 1024)

        val endIndex: Int
            get() = startIndex + writeIndex

        fun get(index: Int): Char {
            return if (index < startIndex && previous != null) {
                previous.get(index)
            } else {
                content[index - startIndex]
            }
        }

        fun get(start: Int, end: Int): String {
            return if (start < startIndex && previous != null) {
                previous.get(start, end)
            } else {
                val endIndex = (end - start) + startIndex
                if (endIndex <= writeIndex) {
                    content.concatToString(start - startIndex, endIndex)
                } else {
                    TODO()
                }
            }
        }

        fun append(chars: CharArray): Buffer {
            val available = content.size - writeIndex
            return if (chars.size <= available) {
                chars.copyInto(content, writeIndex, 0, chars.size)
                writeIndex += chars.size
                this
            } else {
                TODO()
            }
        }
    }
}