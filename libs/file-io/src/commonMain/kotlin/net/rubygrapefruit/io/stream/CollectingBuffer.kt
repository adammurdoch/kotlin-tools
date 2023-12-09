package net.rubygrapefruit.io.stream

import net.rubygrapefruit.file.FailedOperation
import net.rubygrapefruit.file.Result
import net.rubygrapefruit.file.Success

class CollectingBuffer {
    private val head = Buffer(0)
    private var current = head

    /**
     * Reads the content of the given steam into this buffer.
     */
    fun readFrom(stream: ReadStream): Result<Any> {
        while (true) {
            var capacity = current.remaining
            if (capacity == 0) {
                val next = Buffer(current.endPos)
                current.next = next
                current = next
                capacity = current.remaining
            }
            val result = current.readFrom(stream, capacity)
            if (result !is Success || !result.get()) {
                return result
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

        fun readFrom(stream: ReadStream, count: Int): Result<Boolean> {
            val result = stream.read(bytes, writePos, count)
            return when (result) {
                is ReadFailed -> return FailedOperation(result.exception)
                is EndOfStream -> Success(false)
                is ReadBytes -> {
                    val nread = result.get()
                    if (nread < 0) {
                        return Success(false)
                    }
                    writePos += nread
                    return Success(true)
                }
            }
        }
    }
}