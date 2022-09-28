package net.rubygrapefruit.file

/**
 * An immutable snapshot of an entry in a directory.
 */
interface DirectoryEntry {
    /**
     * The name of the entry.
     */
    val name: String

    /**
     * The type of the entry.
     */
    val type: ElementType

    /**
     * View the entry as a `Directory`.
     */
    fun toDir(): Directory

    /**
     * View the entry as a `FileSystemElement`.
     */
    fun toElement(): FileSystemElement
}
