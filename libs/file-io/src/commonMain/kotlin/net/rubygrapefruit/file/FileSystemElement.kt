package net.rubygrapefruit.file

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
