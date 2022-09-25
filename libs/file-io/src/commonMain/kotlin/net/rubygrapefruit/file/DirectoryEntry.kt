package net.rubygrapefruit.file

interface DirectoryEntry {
    /**
     * The name of the entry,
     */
    val name: String

    /**
     * The type of the entry.
     */
    val type: ElementType
}
