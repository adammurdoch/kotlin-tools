package net.rubygrapefruit.io

import kotlinx.io.Buffer
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlin.math.min

/**
 * Transfers the given number of bytes from this buffer to some consumer.
 */
@UnsafeIoApi
inline fun Buffer.readFrom(byteCount: Long, consumer: (ByteArray, Int, Int) -> Unit) {
    if (byteCount == 0L) {
        return
    }
    var remaining = byteCount
    UnsafeBufferOperations.readFromHead(this) { buffer, startIndex, endIndex ->
        val max = if (remaining > Int.MAX_VALUE) Int.MAX_VALUE else remaining.toInt()
        val available = endIndex - startIndex
        val count = min(max, available)
        consumer(buffer, startIndex, count)
        remaining -= count
        count
    }
}

/**
 * Transfers at least 1 and up to the given number of bytes to this buffer from some consumer.
 */
@UnsafeIoApi
inline fun Buffer.writeAtMostTo(byteCount: Long, producer: (ByteArray, Int, Int) -> Int): Int {
    val requested = if (byteCount > Int.MAX_VALUE) Int.MAX_VALUE else byteCount.toInt()
    return UnsafeBufferOperations.writeToTail(this, 1) { buffer, startIndex, endIndex ->
        val available = endIndex - startIndex
        val count = min(requested, available)
        val nread = producer(buffer, startIndex, count)
        if (nread < 0) 0 else nread
    }
}