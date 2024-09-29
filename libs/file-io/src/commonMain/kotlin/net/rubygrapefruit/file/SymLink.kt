package net.rubygrapefruit.file

/**
 * A symlink in the file system.
 */
interface SymLink : FileSystemElement {
    override val parent: Directory

    /**
     * Reads the symlink target.
     */
    fun readSymLink(): Result<String>

    /**
     * Resolves this symlink to a regular file.
     */
    fun resolveFile(): RegularFile {
        var current = this
        while (true) {
            val path = current.readSymLink()
            val target = parent.path.resolve(path.get())
            val snapshot = target.snapshot()
            if (snapshot is Success && snapshot.get().metadata is SymlinkMetadata) {
                current = parent.symLink(target.absolutePath)
            } else {
                return parent.file(target.absolutePath)
            }
        }
    }

    /**
     * Creates a symlink or updates its target.
     */
    @Throws(FileSystemException::class)
    fun writeSymLink(target: String)
}