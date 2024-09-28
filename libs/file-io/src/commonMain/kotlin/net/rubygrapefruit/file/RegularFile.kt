package net.rubygrapefruit.file

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readString
import kotlinx.io.writeString
import net.rubygrapefruit.io.ResourceResult
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
     * Applies a batch of random access reads or writes to this file.
     * The stream is positioned at the start of the file.
     *
     * Creates the file if it does not exist.
     */
    fun <T> withContent(action: (FileContent) -> T): Result<T>

    /**
     * Opens the content of this file for random access reads or writes.
     * The stream is positioned at the start of the file.
     *
     * Creates the file if it does not exist.
     *
     * The caller is responsible for closing the contents.
     */
    fun openContent(): ResourceResult<FileContent>

    /**
     * Writes zero or more bytes to the file, replacing any existing content.
     */
    @Throws(FileSystemException::class)
    fun write(action: (Sink) -> Unit)

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
        write { sink -> sink.write(bytes) }
    }

    /**
     * Writes the given text to the file, replacing any existing content. The text is encoded using UTF-8.
     */
    @Throws(FileSystemException::class)
    fun writeText(text: String) {
        write { sink -> sink.writeString(text) }
    }

    /**
     * Reads bytes from the file.
     */
    @Throws(FileSystemException::class)
    fun <T> read(action: (Source) -> Result<T>): Result<T>

    /**
     * Reads bytes from the file.
     *
     * The [ReadStream] is not buffered.
     */
    fun <T> readBytes(action: (ReadStream) -> Result<T>): Result<T>

    /**
     * Reads bytes from the file into a [ByteArray].
     */
    @Throws(FileSystemException::class)
    fun readBytes(): Result<ByteArray> {
        return read { source -> Success(source.readByteArray()) }
    }

    /**
     * Reads text from the file. The file content is decoded using UTF-8.
     */
    @Throws(FileSystemException::class)
    fun readText(): Result<String> {
        return read { source -> Success(source.readString()) }
    }
}
