package net.rubygrapefruit.io.stream

import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.Try

class CollectingBuffer {
    private val head = Buffer(0)
    private var current = head

    /**
     * Reads the content of the given steam into this buffer.
     */
    fun readFrom(stream: ReadStream): Try<Unit, IOException> {
        while (true) {
            var capacity = current.remaining
            if (capacity == 0) {
                val next = Buffer(current.endPos)
                current.next = next
                current = next
                capacity = current.remaining
            }
            val result = current.readFrom(stream, capacity)
            when (result) {
                is EndOfStream -> return Try.succeeded(Unit)
                is ReadFailed -> return Try.failed(result.exception)
                is ReadBytes -> {
                    // Continue
                }
            }
        }
    }

    fun toByteArray(): ByteArray {
        val result = ByteArray(current.endPos)
        var buffer: Buffer? = head
        var pos = 0
        while (buffer != null) {
            buffer.bytes.copyInto(result, pos, 0, buffer.writePos)
            pos += buffer.writePos
            buffer = buffer.next
        }
        return result
    }

    fun decodeToString(): String {
        return if (current == head) {
            current.bytes.decodeToString(0, current.writePos)
        } else {
            toByteArray().decodeToString()
        }
    }

    private class Buffer(val startPos: Int) {
        val bytes = ByteArray(4096)
        var next: Buffer? = null
        var writePos = 0

        val endPos: Int
            get() = startPos + writePos

        val remaining: Int
            get() = bytes.size - writePos

        fun readFrom(stream: ReadStream, count: Int): ReadResult {
            val result = stream.read(bytes, writePos, count)
            return when (result) {
                is ReadBytes -> {
                    writePos += result.get()
                    result
                }

                is ReadFailed, EndOfStream -> result
            }
        }
    }
}