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
}
