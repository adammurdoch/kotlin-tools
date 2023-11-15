package net.rubygrapefruit.file

/**
 * An immutable snapshot of an entry in a directory.
 */
interface DirectoryEntry : HasPath {
    /**
     * The type of the entry.
     */
    val type: ElementType
}
