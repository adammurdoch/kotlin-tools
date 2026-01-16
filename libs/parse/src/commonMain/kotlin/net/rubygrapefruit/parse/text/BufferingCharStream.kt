package net.rubygrapefruit.parse.text


internal class BufferingCharStream(bufferLen: Int = 64 * 1024) : AdvancingCharStream {
    private var tail = Buffer(null, 0, bufferLen)
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
        tail = tail.append(chars, 0, chars.size)
    }

    override fun advance(count: Int) {
        pos += count
    }

    fun end() {
        finished = true
    }

    private class Buffer(private val previous: Buffer?, private val startIndex: Int, bufferLen: Int) {
        private var writeIndex = 0
        private val content = CharArray(bufferLen)

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
            return if (start >= startIndex || previous == null) {
                val regionEnd = end - startIndex
                val regionStart = start - startIndex
                content.concatToString(regionStart, regionEnd)
            } else if (end <= startIndex) {
                previous.get(start, end)
            } else {
                val target = CharArray(end - start)
                previous.get(start, startIndex, target)
                content.copyInto(target, startIndex - start, 0, end - startIndex)
                target.concatToString()
            }
        }

        fun get(start: Int, end: Int, target: CharArray) {
            if (start >= startIndex || previous == null) {
                content.copyInto(target, 0, start, end)
            } else {
                previous.get(start, startIndex, target)
                content.copyInto(target, startIndex - start, 0, end - startIndex)
            }
        }

        fun append(chars: CharArray, start: Int, end: Int): Buffer {
            if (end == start) {
                return this
            }

            val available = content.size - writeIndex
            val count = end - start
            return if (count <= available) {
                chars.copyInto(content, writeIndex, start, end)
                writeIndex += count
                this
            } else if (available == 0) {
                val next = Buffer(this, startIndex + writeIndex, content.size)
                next.append(chars, start, end)
            } else {
                chars.copyInto(content, writeIndex, start, start + available)
                writeIndex += available
                val next = Buffer(this, startIndex + writeIndex, content.size)
                next.append(chars, start + available, end)
            }
        }
    }
}