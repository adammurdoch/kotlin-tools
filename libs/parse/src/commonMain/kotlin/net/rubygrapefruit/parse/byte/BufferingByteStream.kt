package net.rubygrapefruit.parse.byte

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

    override fun posAt(index: Int): BytePosition {
        return BytePosition(index + pos)
    }

    fun append(bytes: ByteArray) {
        tail = tail.append(bytes)
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

        fun append(bytes: ByteArray): Buffer {
            val available = content.size - writeIndex
            return if (bytes.size <= available) {
                bytes.copyInto(content, writeIndex, 0, bytes.size)
                writeIndex += bytes.size
                this
            } else {
                TODO()
            }
        }
    }
}