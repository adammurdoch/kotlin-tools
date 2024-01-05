package net.rubygrapefruit.file

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