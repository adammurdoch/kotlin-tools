package net.rubygrapefruit.file

import net.rubygrapefruit.io.TryFailure
import net.rubygrapefruit.io.stream.CollectingBuffer
import net.rubygrapefruit.io.stream.ReadStream
import net.rubygrapefruit.io.stream.WriteStream

/**
 * Represents an element in the file system.
 */
sealed interface FileSystemElement : HasPath {
    /**
     * Returns the parent of this element, or null if this element is the root of the file system
     */
    val parent: Directory?

    /**
     * Returns a snapshot of the current metadata of the file. Does not follow symlinks.
     */
    fun metadata(): Result<ElementMetadata>

    /**
     * Returns a snapshot of the current permissions. Does not follow symlinks.
     */
    fun posixPermissions(): Result<PosixPermissions>

    /**
     * Sets the permissions of this element. Note that when this element is a symlink, the permissions of the symlink itself are changed.
     */
    @Throws(FileSystemException::class)
    fun setPermissions(permissions: PosixPermissions)

    /**
     * Does this file system element have the specified capability?
     */
    fun supports(capability: FileSystemCapability): Boolean
}

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
     * Writes zero or more bytes to the file, replacing its content.
     *
     * The [WriteStream] is not buffered.
     */
    @Throws(FileSystemException::class)
    fun writeBytes(action: (WriteStream) -> Unit)

    /**
     * Writes the given bytes to the file, replacing its content.
     */
    @Throws(FileSystemException::class)
    fun writeBytes(bytes: ByteArray) {
        writeBytes { stream -> stream.write(bytes) }
    }

    /**
     * Writes the given text to the file, replacing it content. The text is encoded using UTF-8.
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

/**
 * A directory in the file system.
 */
interface Directory : FileSystemElement {
    /**
     * Resolves a file relative to this directory. Note: does not check whether the file exists or is a regular file.
     */
    fun file(name: String): RegularFile

    /**
     * Resolves a directory relative to this directory. Note: does not check whether the file exists or is a directory.
     */
    fun dir(name: String): Directory

    /**
     * Resolves a symlink relative to this directory. Note: does not check whether the file exists or is a symlink.
     */
    fun symLink(name: String): SymLink

    /**
     * Deletes this directory and all of its entries recursively, if the directory exists.
     * Does nothing if the directory does not exist.
     *
     * Does not follow symlinks.
     *
     * Note: the implementation is not guaranteed to be atomic.
     */
    @Throws(FileSystemException::class)
    fun deleteRecursively()

    /**
     * Creates this directory and its ancestors if they do not exist.
     * Does nothing if the directory already exists.
     */
    @Throws(FileSystemException::class)
    fun createDirectories()

    /**
     * Creates a new temporary directory in this directory.
     * Note: the implementation is not guaranteed to be atomic.
     */
    @Throws(FileSystemException::class)
    fun createTemporaryDirectory(): Directory

    /**
     * Returns a snapshot of the entries in this directory. Does not follow symlinks in the entries.
     */
    fun listEntries(): Result<List<DirectoryEntry>>

    /**
     * Visits the contents of this directory tree. Visits a directory before its entries.
     *
     * Does not follow symlinks.
     */
    fun visitTopDown(visitor: DirectoryEntry.() -> Unit)
}

/**
 * A symlink in the file system.
 */
interface SymLink : FileSystemElement {
    /**
     * Reads the symlink target.
     */
    fun readSymLink(): Result<String>

    /**
     * Creates a symlink or updates its target.
     */
    @Throws(FileSystemException::class)
    fun writeSymLink(target: String)
}