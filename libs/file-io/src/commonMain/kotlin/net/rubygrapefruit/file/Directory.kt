package net.rubygrapefruit.file

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
     *
     * Note: the implementation is not guaranteed to be atomic.
     */
    @Throws(FileSystemException::class)
    fun createTemporaryDirectory(): Directory

    /**
     * Returns a snapshot of the entries in this directory. Does not follow symlinks in the entries.
     *
     * Note: the implementation is not guaranteed to be atomic.
     *
     * @throws MissingDirectoryException When the directory cannot be listed because it or one of its ancestors does not exist.
     * @throws DirectoryPermissionException When the directory cannot be listed because the current process does not have the appropriate permissions to list the directory contents.
     * @throws FileSystemException When the directory cannot be listed for some other reason.
     */
    @Throws(FileSystemException::class)
    fun listEntries(): List<DirectoryEntry>

    /**
     * Visits the contents of this directory tree. Visits a directory before its entries.
     *
     * Does not follow symlinks.
     */
    @Throws(FileSystemException::class)
    fun visitTopDown(visitor: DirectoryEntry.() -> Unit)
}