package net.rubygrapefruit.file

/**
 * A reference to an element in the file system.
 */
expect sealed interface FileSystemElement {
    /**
     * Returns the parent of this element, or null if this element is the root of the file system
     */
    val parent: Directory?

    /**
     * Returns the base name of this element.
     */
    val name: String

    /**
     * Returns the absolute path of this element.
     */
    val absolutePath: String

    /**
     * Returns a snapshot of the current metadata of the file.
     */
    fun metadata(): ElementMetadata

    /**
     * Returns this element as a resolve result, which contains the path of this element and a snapshot of its metadata.
     */
    fun resolve(): ElementResolveResult
}

/**
 * A regular file in the file system.
 */
interface RegularFile : FileSystemElement {
    /**
     * Writes the given text to the file, using UTF-8 encoding.
     */
    fun writeText(text: String)

    /**
     * Reads text from the file, using UTF-8 encoding.
     */
    fun readText(): String
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
     * Resolves a name relative to this directory and queries the element's type and basic metadata.
     */
    fun resolve(name: String): ElementResolveResult

    /**
     * Creates this directory and its ancestors if they do not exist.
     */
    fun createDirectories()

    /**
     * Creates a new temporary directory in this directory.
     */
    fun createTemporaryDirectory(): Directory

    /**
     * Returns a snapshot of the entries in this directory.
     */
    fun listEntries(): List<DirectoryEntry>
}