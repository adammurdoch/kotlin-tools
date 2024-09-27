package net.rubygrapefruit.file

import kotlinx.io.RawSink
import kotlinx.io.RawSource
import net.rubygrapefruit.io.stream.ReadStream
import net.rubygrapefruit.io.stream.WriteStream

interface FileContent {
    /**
     * Returns the current position in the file where the next read or write will happen.
     */
    val currentPosition: Long

    /**
     * Returns a [WriteStream] that writes to the current position in the file.
     */
    val writeStream: WriteStream

    /**
     * Returns a [ReadStream] that reads from the current position in the file.
     */
    val readStream: ReadStream

    /**
     * Returns a [RawSink] that writes to the current position in the file.
     *
     * The [RawSink] is not buffered.
     */
    val sink: RawSink

    /**
     * Returns a [RawSource] that reads from the current position in the file.
     *
     * The [RawSource] is not buffered.
     */
    val source: RawSource

    /**
     * Returns the current file length.
     */
    fun length(): Long

    /**
     * Moves the current position to the given absolute position.
     */
    fun seek(position: Long)

    /**
     * Moves the current position to the end of the file
     *
     * @return the position after moving
     */
    fun seekToEnd(): Long
}