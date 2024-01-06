package net.rubygrapefruit.file

import net.rubygrapefruit.io.TryFailure
import net.rubygrapefruit.io.stream.CollectingBuffer
import net.rubygrapefruit.io.stream.ReadStream
import net.rubygrapefruit.io.stream.WriteStream

/**
 * A regular file in the file system.
 */
interface RegularFile : FileSystemElement {
    /**
     * Deletes this file, if it exists.
     */
    @Throws(FileSystemException::class)
    fun delete()

    /**
     * Applies random-access reads or writes to this file. The stream is positioned at the start of the file.
     *
     * Creates the file if it does not exist.
     */
    fun <T> withContent(action: (FileContent) -> T): Result<T> {
        TODO()
    }

    /**
     * Writes zero or more bytes to the file, replacing any existing content.
     *
     * The [WriteStream] is not buffered.
     */
    @Throws(FileSystemException::class)
    fun writeBytes(action: (WriteStream) -> Unit)

    /**
     * Writes the given bytes to the file, replacing any existing content.
     */
    @Throws(FileSystemException::class)
    fun writeBytes(bytes: ByteArray) {
        writeBytes { stream -> stream.write(bytes) }
    }

    /**
     * Writes the given text to the file, replacing any existing content. The text is encoded using UTF-8.
     */
    @Throws(FileSystemException::class)
    fun writeText(text: String) {
        writeBytes { stream -> stream.write(text.encodeToByteArray()) }
    }

    /**
     * Reads bytes from the file.
     *
     * The [ReadStream] is not buffered.
     */
    fun <T> readBytes(action: (ReadStream) -> Result<T>): Result<T>

    /**
     * Reads bytes from the file into a [ByteArray].
     */
    fun readBytes(): Result<ByteArray> {
        return readIntoBuffer().map { buffer -> buffer.toByteArray() }
    }

    /**
     * Reads text from the file. The file content is decoded using UTF-8.
     */
    fun readText(): Result<String> {
        return readIntoBuffer().map { buffer -> buffer.decodeToString() }
    }
}

internal fun RegularFile.readIntoBuffer(): Result<CollectingBuffer> {
    return readBytes { stream ->
        val buffer = CollectingBuffer()
        val result = buffer.appendFrom(stream)
        if (result is TryFailure) {
            FailedOperation(result.exception)
        } else {
            Success(buffer)
        }
    }
}
