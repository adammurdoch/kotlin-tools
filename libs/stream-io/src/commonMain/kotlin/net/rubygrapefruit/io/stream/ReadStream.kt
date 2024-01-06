package net.rubygrapefruit.io.stream

import net.rubygrapefruit.io.IOException

/**
 * Stream implementations are not thread safe.
 */
interface ReadStream {
    /**
     * Reads available bytes from this stream, up to the given maximum.
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

    /**
     * Reads the given number of bytes from this stream.
     *
     * @throws IOException
     */
    fun readFully(buffer: ByteArray, offset: Int, count: Int) {
        var remaining = count
        var pos = 0
        while (remaining > 0) {
            val result = read(buffer, pos, remaining)
            when (result) {
                is ReadBytes -> {
                    remaining -= result.get()
                    pos += result.get()
                }

                EndOfStream -> throw IOException("Unexpected end of file")
                is ReadFailed -> result.rethrow()
            }
        }
    }

    /**
     * Fills the given buffer from this stream.
     *
     * @throws IOException
     */
    fun readFully(buffer: ByteArray) {
        readFully(buffer, 0, buffer.size)
    }
}