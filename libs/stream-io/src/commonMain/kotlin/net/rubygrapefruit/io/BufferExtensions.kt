package net.rubygrapefruit.io

import kotlinx.io.Buffer
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlin.math.min

@UnsafeIoApi
inline fun Buffer.write(byteCount: Long, sink: (ByteArray, Int, Int) -> Unit) {
    val requested = if (byteCount > Int.MAX_VALUE) Int.MAX_VALUE else byteCount.toInt()
    if (requested == 0) {
        return
    }
    UnsafeBufferOperations.readFromHead(this) { buffer, startIndex, endIndex ->
        val available = endIndex - startIndex
        val count = min(requested, available)
        sink(buffer, startIndex, count)
        count
    }
}