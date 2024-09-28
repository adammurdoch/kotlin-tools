package net.rubygrapefruit.file

import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import net.rubygrapefruit.io.writeAtMostTo
import java.io.InputStream

@OptIn(UnsafeIoApi::class)
internal class InputStreamBackedRawSource(
    private val inputStream: InputStream
) : RawSource {
    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        val ncopied = sink.writeAtMostTo(byteCount) { buffer, startIndex, count ->
            val nread = inputStream.read(buffer, startIndex, count)
            if (nread < 0) 0 else nread
        }
        return if (ncopied == 0) -1 else ncopied.toLong()
    }

    override fun close() {
        inputStream.close()
    }
}