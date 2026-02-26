package net.rubygrapefruit.parse.binary

internal class BufferingByteStream : AdvancingByteStream {
    private var tail = Buffer(null, 0)
    private var pos = 0

    override var finished: Boolean = false
        private set

    override val available: Int
        get() = tail.endIndex - pos

    override fun get(index: Int): Byte {
        return tail.get(pos + index)
    }

    override fun get(start: Int, end: Int): ByteArray {
        return tail.get(start + pos, end + pos)
    }

    override fun posAt(index: Int): BytePosition {
        return BytePosition(index + pos)
    }

    fun append(bytes: ByteArray) {
        tail = tail.append(bytes, 0, bytes.size)
    }

    fun append(bytes: ByteArray, offset: Int, count: Int) {
        tail = tail.append(bytes, offset, count)
    }

    override fun advance(count: Int) {
        pos += count
    }

    fun end() {
        finished = true
    }

    private class Buffer(private val previous: Buffer?, private val startIndex: Int) {
        private var writeIndex = 0
        private val content = ByteArray(64 * 1024)

        val endIndex: Int
            get() = startIndex + writeIndex

        fun get(index: Int): Byte {
            return if (index < startIndex && previous != null) {
                previous.get(index)
            } else {
                content[index - startIndex]
            }
        }

        fun get(start: Int, end: Int): ByteArray {
            return if (start >= startIndex || previous == null) {
                val regionEnd = end - startIndex
                val regionStart = start - startIndex
                content.copyOfRange(regionStart, regionEnd)
            } else if (end <= startIndex) {
                previous.get(start, end)
            } else {
                val target = ByteArray(end - start)
                previous.getInto(start, startIndex, target)
                content.copyInto(target, startIndex - start, 0, end - startIndex)
                target
            }
        }

        fun getInto(start: Int, end: Int, target: ByteArray) {
            if (start >= startIndex || previous == null) {
                content.copyInto(target, 0, start, end)
            } else {
                previous.getInto(start, startIndex, target)
                content.copyInto(target, startIndex - start, 0, end - startIndex)
            }
        }

        fun append(bytes: ByteArray, offset: Int, count: Int): Buffer {
            val available = content.size - writeIndex
            return if (count <= available) {
                bytes.copyInto(content, writeIndex, offset, offset + count)
                writeIndex += count
                this
            } else {
                TODO()
            }
        }
    }
}