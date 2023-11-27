package net.rubygrapefruit.io.stream

import net.rubygrapefruit.file.FileSystemException

interface WriteStream {
    /**
     * Attempts to write the given bytes to this stream.
     */
    @Throws(FileSystemException::class)
    fun write(bytes: ByteArray, offset: Int, count: Int)

    /**
     * Attempts to write the given bytes to this stream.
     */
    @Throws(FileSystemException::class)
    fun write(bytes: ByteArray) {
        write(bytes, 0, bytes.size)
    }
}