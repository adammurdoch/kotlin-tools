package net.rubygrapefruit.io.stream

import net.rubygrapefruit.io.IOException

/**
 * Stream implementations are not thread safe.
 */
interface WriteStream {
    /**
     * Attempts to write the given bytes to this stream. Fails when [count] bytes cannot be written.
     */
    @Throws(IOException::class)
    fun write(bytes: ByteArray, offset: Int, count: Int)

    /**
     * Attempts to write the given bytes to this stream. Fails when the entire buffer cannot be written.
     */
    @Throws(IOException::class)
    fun write(bytes: ByteArray) {
        write(bytes, 0, bytes.size)
    }
}