package net.rubygrapefruit.io.stream

import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import net.rubygrapefruit.io.writeAtMostTo
import java.io.InputStream

@OptIn(UnsafeIoApi::class)
class InputStreamBackedRawSource(
    private val streamSource: StreamSource,
    private val inputStream: InputStream
) : RawSource {
    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        val ncopied = sink.writeAtMostTo(byteCount) { buffer, startIndex, count ->
            val nread = try {
                inputStream.read(buffer, startIndex, count)
            } catch (e: Exception) {
                throw streamSource.readFailed(e)
            }
            if (nread < 0) 0 else nread
        }
        return if (ncopied == 0) -1 else ncopied.toLong()
    }

    override fun close() {
        inputStream.close()
    }
}