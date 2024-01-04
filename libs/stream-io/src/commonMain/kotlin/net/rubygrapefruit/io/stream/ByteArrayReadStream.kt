package net.rubygrapefruit.io.stream

import kotlin.math.min

class ByteArrayReadStream(
    private val bytes: ByteArray
) : ReadStream {
    private var readPos = 0

    override fun read(buffer: ByteArray, offset: Int, max: Int): ReadResult {
        val remaining = bytes.size - readPos
        return if (remaining == 0) {
            EndOfStream
        } else {
            val count = min(remaining, max)
            bytes.copyInto(buffer, offset, readPos, readPos + count)
            readPos += count
            ReadBytes(count)
        }
    }
}