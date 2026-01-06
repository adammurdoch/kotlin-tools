package net.rubygrapefruit.parse.char

internal class BufferingCharStream : CharStream {
    private var tail = Buffer(null, 0)

    override val length: Int
        get() = tail.endIndex

    override fun get(index: Int): Char {
        return tail.get(index)
    }

    fun append(chars: CharArray) {
        tail = tail.append(chars)
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