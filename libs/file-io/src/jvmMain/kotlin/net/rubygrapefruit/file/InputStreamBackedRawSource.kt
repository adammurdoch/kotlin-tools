package net.rubygrapefruit.file

import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import java.io.InputStream
import kotlin.math.min

@OptIn(UnsafeIoApi::class)
internal class InputStreamBackedRawSource(
    private val inputStream: InputStream
) : RawSource {
    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        val ncopied = UnsafeBufferOperations.writeToTail(sink, 1) { buffer, startIndex, endIndex ->
            val count = min(byteCount.toInt(), endIndex - startIndex)
            val nread = inputStream.read(buffer, startIndex, count)
            if (nread < 0) 0 else nread
        }
        return if (ncopied == 0) -1 else ncopied.toLong()
    }

    override fun close() {
        inputStream.close()
    }
}