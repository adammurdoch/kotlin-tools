package net.rubygrapefruit.file

import net.rubygrapefruit.io.stream.*
import java.io.InputStream

internal class InputStreamBackedReadStream(
    private val owner: JvmRegularFile,
    private val inputStream: InputStream
) : ReadStream {
    override fun read(buffer: ByteArray, offset: Int, max: Int): ReadResult {
        val nread = try {
            inputStream.read(buffer, offset, max)
        } catch (e: Exception) {
            return ReadFailed(readFile<Any>(owner, cause = e).failure)
        }
        return if (nread < 0) {
            EndOfStream
        } else {
            ReadBytes(nread)
        }
    }
}