package net.rubygrapefruit.io.stream

import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.Try
import kotlin.math.min

/**
 * Collects a sequence of bytes in memory.
 */
class CollectingBuffer {
    private val head = Buffer(0)
    private var current = head

    /**
     * Appends the content of the given steam to this buffer.
     */
    fun appendFrom(stream: ReadStream): Try<Unit, IOException> {
        while (true) {
            if (current.remaining == 0) {
                expand()
            }
            val capacity = current.remaining
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

    fun append(bytes: ByteArray, offset: Int, count: Int) {
        var remaining = count
        var pos = offset
        while (remaining > 0) {
            if (current.remaining == 0) {
                expand()
            }
            val copied = min(current.remaining, remaining)
            current.append(bytes, pos, copied)
            pos += copied
            remaining -= copied
        }
    }

    private fun expand() {
        val next = Buffer(current.endPos)
        current.next = next
        current = next
    }

    /**
     * Converts the contents of this buffer into a [ByteArray].
     */
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

    /**
     * Decodes the convents of this buffer into a string using UTF-8 encoding.
     */
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

        fun append(bytes: ByteArray, offset: Int, count: Int) {
            bytes.copyInto(this.bytes, writePos, offset, offset + count)
            writePos += count
        }
    }
}