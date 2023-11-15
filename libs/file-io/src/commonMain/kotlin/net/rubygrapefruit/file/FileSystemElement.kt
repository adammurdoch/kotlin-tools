package net.rubygrapefruit.file

/**
 * A reference to an element in the file system.
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
     * Returns a snapshot of the element, which contains the path of this element and a snapshot of its metadata. Does not follow symlinks.
     */
    fun snapshot(): Result<ElementSnapshot>

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
     * Writes the given text to the file, using UTF-8 encoding.
     */
    @Throws(FileSystemException::class)
    fun writeText(text: String)

    /**
     * Reads text from the file, using UTF-8 encoding.
     */
    fun readText(): Result<String>
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
     * Resolves a name relative to this directory. Note: does not determine the type.
     */
    fun resolve(name: String): ElementPath

    /**
     * Deletes this directory and all of its entries recursively, if the directory exists.
     */
    @Throws(FileSystemException::class)
    fun deleteRecursively()

    /**
     * Creates this directory and its ancestors if they do not exist.
     */
    @Throws(FileSystemException::class)
    fun createDirectories()

    /**
     * Creates a new temporary directory in this directory.
     */
    @Throws(FileSystemException::class)
    fun createTemporaryDirectory(): Directory

    /**
     * Returns a snapshot of the entries in this directory. Does not follow symlinks in the entries.
     */
    fun listEntries(): Result<List<DirectoryEntry>>

    /**
     * Visits the contents of this directory tree. Visits a directory before its entries.
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