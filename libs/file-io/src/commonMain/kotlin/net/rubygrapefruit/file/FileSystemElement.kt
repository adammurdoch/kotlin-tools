package net.rubygrapefruit.file

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
}

expect class RegularFile : FileSystemElement {
    /**
     * Writes the given text to the file, using UTF-8 encoding.
     */
    fun writeText(text: String)
}

expect class Directory : FileSystemElement {
    companion object {
        /**
         * Locates the current directory of this process.
         */
        val current: Directory
    }

    /**
     * Creates this directory and its ancestors if they do not exist.
     */
    fun createDirectories()

    /**
     * Locates a file with the given name in this directory.
     */
    fun file(name: String): RegularFile

    /**
     * Locates a directory with the given name in this directory.
     */
    fun dir(name: String): Directory

    /**
     * Creates a new temporary directory in this directory.
     */
    fun createTemporaryDirectory(): Directory
}