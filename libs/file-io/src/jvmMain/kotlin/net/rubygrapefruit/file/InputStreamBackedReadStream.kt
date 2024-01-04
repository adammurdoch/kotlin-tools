package net.rubygrapefruit.file

import net.rubygrapefruit.io.stream.EndOfStream
import net.rubygrapefruit.io.stream.ReadBytes
import net.rubygrapefruit.io.stream.ReadResult
import net.rubygrapefruit.io.stream.ReadStream
import java.io.InputStream

internal class InputStreamBackedReadStream(
    private val inputStream: InputStream
) : ReadStream {
    override fun read(buffer: ByteArray, offset: Int, max: Int): ReadResult {
        val nread = inputStream.read(buffer, offset, max)
        return if (nread < 0) {
            EndOfStream
        } else {
            ReadBytes(nread)
        }
    }
}