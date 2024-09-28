package net.rubygrapefruit.file

import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.Sink
import kotlinx.io.buffered

interface FileContent {
    /**
     * Returns the current position in the file where the next read or write will happen.
     */
    val currentPosition: Long

    /**
     * Returns a [RawSink] that writes to the current position in the file.
     *
     * The [RawSink] is not buffered.
     */
    val sink: RawSink

    /**
     * Writes zero or more bytes to the file at the current position.
     */
    fun write(action: (Sink) -> Unit) {
        val sink = sink.buffered()
        action(sink)
        sink.flush()
    }

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