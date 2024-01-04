package net.rubygrapefruit.io.stream

/**
 * Stream implementations are not thread safe.
 */
interface ReadStream {
    /**
     * Reads available bytes from this stream.
     *
     * @return [ReadBytes] when some bytes read into the buffer, or [EndOfStream] when the end of the stream is reached.
     */
    fun read(buffer: ByteArray, offset: Int, max: Int): ReadResult

    /**
     * Reads available bytes from this stream.
     *
     * @return [ReadBytes] when some bytes read into the buffer, or [EndOfStream] when the end of the stream is reached.
     */
    fun read(buffer: ByteArray): ReadResult {
        return read(buffer, 0, buffer.size)
    }
}