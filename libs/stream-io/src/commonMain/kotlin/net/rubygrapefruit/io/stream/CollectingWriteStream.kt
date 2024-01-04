package net.rubygrapefruit.io.stream

class CollectingWriteStream : WriteStream {
    private val buffer = CollectingBuffer()

    override fun write(bytes: ByteArray, offset: Int, count: Int) {
        buffer.append(bytes, offset, count)
    }

    fun toByteArray(): ByteArray {
        return buffer.toByteArray()
    }
}