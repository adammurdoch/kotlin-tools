package net.rubygrapefruit.file

import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.UnsafeIoApi
import net.rubygrapefruit.io.write
import java.io.OutputStream

@OptIn(UnsafeIoApi::class)
internal class OutputStreamBackedRawSink(
    private val outputStream: OutputStream
) : RawSink {
    override fun write(source: Buffer, byteCount: Long) {
        source.write(byteCount) { buffer, startIndex, count ->
            outputStream.write(buffer, startIndex, count)
        }
    }

    override fun flush() {
    }

    override fun close() {
        outputStream.close()
    }
}