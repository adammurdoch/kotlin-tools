package net.rubygrapefruit.file

import net.rubygrapefruit.io.stream.ReadStream
import net.rubygrapefruit.io.stream.WriteStream

interface FileContent {
    /**
     * Returns the current position in the file where the next read or write will happen.
     */
    val currentPosition: UInt

    /**
     * Returns a [WriteStream] that writes to the current position in the file.
     */
    val writeStream: WriteStream

    /**
     * Returns a [ReadStream] that reads from the current position in the file.
     */
    val readStream: ReadStream

    /**
     * Returns the current file length.
     */
    fun length(): UInt

    /**
     * Moves the current position to the given absolute position.
     */
    fun seek(position: UInt)

    /**
     * Moves the current position to the end of the file
     */
    fun seekToEnd()
}