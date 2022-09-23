package net.rubygrapefruit.file

/**
 * Some element in the file system.
 */
expect sealed class FileSystemElement {
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
     * Get a snapshot of the current metadata of the file.
     */
    fun metadata(): FileSystemElementMetadata
}

/**
 * A regular file in the file system.
 */
expect class RegularFile : FileSystemElement {
    /**
     * Writes the given text to the file, using UTF-8 encoding.
     */
    fun writeText(text: String)
}

/**
 * A directory in the file system.
 */
expect class Directory : FileSystemElement {
    companion object {
        /**
         * The current directory of this process.
         */
        val current: Directory

        /**
         * The user's home directory.
         */
        val userHome: Directory
    }

    /**
     * Creates this directory and its ancestors if they do not exist.
     */
    fun createDirectories()

    /**
     * Locates a file relative to this directory.
     */
    fun file(name: String): RegularFile

    /**
     * Locates a directory relative to this directory.
     */
    fun dir(name: String): Directory

    /**
     * Creates a new temporary directory in this directory.
     */
    fun createTemporaryDirectory(): Directory
}