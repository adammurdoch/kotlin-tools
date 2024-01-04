package net.rubygrapefruit.file

import net.rubygrapefruit.io.stream.WriteStream
import java.io.OutputStream

internal class OutputStreamBackedWriteStream(
    private val outputStream: OutputStream
) : WriteStream {
    override fun write(bytes: ByteArray, offset: Int, count: Int) {
        outputStream.write(bytes, offset, count)
    }
}