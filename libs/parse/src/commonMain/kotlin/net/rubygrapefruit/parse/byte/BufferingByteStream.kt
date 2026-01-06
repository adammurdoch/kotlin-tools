package net.rubygrapefruit.parse.byte

internal class BufferingByteStream : ByteStream {
    private var tail = Buffer(null, 0)

    override val length: Int
        get() = tail.endIndex

    override fun get(index: Int): Byte {
        return tail.get(index)
    }

    fun append(bytes: ByteArray) {
        tail = tail.append(bytes)
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