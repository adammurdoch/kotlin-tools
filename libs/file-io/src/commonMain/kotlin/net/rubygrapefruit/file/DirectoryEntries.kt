package net.rubygrapefruit.file

sealed class DirectoryEntries

object MissingDirectoryEntries : DirectoryEntries()

object UnreadableDirectoryEntries : DirectoryEntries()

class ExistingDirectoryEntries(val entries: List<DirectoryEntry>) : DirectoryEntries()

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
