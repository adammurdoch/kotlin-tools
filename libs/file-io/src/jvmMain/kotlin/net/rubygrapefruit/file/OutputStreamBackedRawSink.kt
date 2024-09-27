package net.rubygrapefruit.file

import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.io.unsafe.withData
import java.io.OutputStream

@OptIn(UnsafeIoApi::class)
internal class OutputStreamBackedRawSink(
    private val outputStream: OutputStream
) : RawSink {
    override fun write(source: Buffer, byteCount: Long) {
        UnsafeBufferOperations.forEachSegment(source) { context, segment ->
            context.withData(segment) { buffer, startIndex, endIndex ->
                outputStream.write(buffer, startIndex, endIndex - startIndex)
            }
        }
    }

    override fun flush() {
    }

    override fun close() {
        outputStream.close()
    }
}