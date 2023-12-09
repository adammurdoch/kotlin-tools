package net.rubygrapefruit.io.stream

/**
 * Stream implementations are not thread safe.
 */
interface ReadStream {
    /**
     * Reads available bytes from this stream.
     *
     * @return The number of bytes read into the buffer, possibly 0.
     */
    fun read(buffer: ByteArray, offset: Int, max: Int): ReadResult
}