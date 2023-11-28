package net.rubygrapefruit.io.stream

import net.rubygrapefruit.file.FileSystemException
import net.rubygrapefruit.file.Result

/**
 * Stream implementations are not thread safe.
 */
interface ReadStream {
    /**
     * Reads available bytes from this stream.
     *
     * @return The number of bytes read into the buffer, possibly 0. Returns -1 on end of stream.
     */
    @Throws(FileSystemException::class)
    fun read(buffer: ByteArray, offset: Int, max: Int): Result<Int>
}