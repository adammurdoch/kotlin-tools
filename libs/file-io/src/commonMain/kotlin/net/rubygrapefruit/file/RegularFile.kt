package net.rubygrapefruit.file

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readString
import kotlinx.io.writeString
import net.rubygrapefruit.io.Resource

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
    @Throws(FileSystemException::class)
    fun <T> withContent(action: (FileContent) -> T): T

    /**
     * Opens the content of this file for random access reads or writes.
     * The stream is positioned at the start of the file.
     *
     * Creates the file if it does not exist.
     *
     * The caller is responsible for closing the contents.
     */
    @Throws(FileSystemException::class)
    fun openContent(): Resource<FileContent>

    /**
     * Writes zero or more bytes to the file. Replaces existing content if the file exists, or creates the file if it does not exist.
     */
    @Throws(FileSystemException::class)
    fun write(action: (Sink) -> Unit)

    /**
     * Writes the given bytes to the file. Replaces existing content if the file exists, or creates the file if it does not exist.
     */
    @Throws(FileSystemException::class)
    fun writeBytes(bytes: ByteArray) {
        write { sink -> sink.write(bytes) }
    }

    /**
     * Writes the given text to the file. Replaces existing content if the file exists, or creates the file if it does not exist.
     * The text is encoded using UTF-8.
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
     * Reads the content of this file into a [ByteArray].
     */
    @Throws(FileSystemException::class)
    fun readBytes(): ByteArray {
        return read { source -> Success(source.readByteArray()) }.get()
    }

    /**
     * Reads the content of this file as text. The file content is decoded using UTF-8.
     */
    @Throws(FileSystemException::class)
    fun readText(): String {
        return read { source -> Success(source.readString()) }.get()
    }
}
