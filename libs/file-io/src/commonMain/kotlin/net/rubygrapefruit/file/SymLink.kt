package net.rubygrapefruit.file

/**
 * A symlink in the file system.
 */
interface SymLink : FileSystemElement {
    override val parent: Directory

    /**
     * Reads the symlink target.
     *
     * @throws SymlinkPermissionException When the current process does not have permission to read the symlink.
     * @throws FileSystemException When the symlink cannot be read for some other reason.
     */
    @Throws(FileSystemException::class)
    fun readSymLink(): String

    /**
     * Resolves this symlink to its target element. Follows symlinks to reach something that is not a symlink
     */
    fun resolve(): Result<ElementSnapshot> {
        var current = this
        while (true) {
            val path = current.readSymLink()
            val target = parent.path.resolve(path)
            val snapshot = target.snapshot()
            if (snapshot is Success && snapshot.get().metadata is SymlinkMetadata) {
                current = parent.symLink(target.absolutePath)
            } else {
                return snapshot
            }
        }
    }

    /**
     * Creates a symlink or updates its target.
     */
    @Throws(FileSystemException::class)
    fun writeSymLink(target: String)
}