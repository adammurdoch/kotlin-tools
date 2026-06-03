package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Position
import net.rubygrapefruit.parse.minus
import net.rubygrapefruit.parse.plus

internal class BufferingByteStream : AdvancingByteStream {
    private var tail = Buffer(null, 0)
    override var position = Position.Zero
        private set

    override var finished: Boolean = false
        private set

    override val available: Int
        get() = tail.endIndex - position

    override fun get(index: Int): Byte {
        return tail.get(index + position)
    }

    override fun get(start: Int, end: Int): ByteArray {
        return tail.get(start + position, end + position)
    }

    override fun posAt(index: Int): BytePosition {
        return BytePosition(position + index)
    }

    fun append(bytes: ByteArray) {
        tail = tail.append(bytes, 0, bytes.size)
    }

    fun append(bytes: ByteArray, offset: Int, count: Int) {
        tail = tail.append(bytes, offset, count)
    }

    override fun advance(count: Int) {
        position += count
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