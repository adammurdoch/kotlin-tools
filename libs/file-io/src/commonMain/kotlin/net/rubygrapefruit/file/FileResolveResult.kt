package net.rubygrapefruit.file

/**
 * An immutable snapshot of a file system element's path, type and basic metadata.
 */
interface FileResolveResult {
    /**
     * Returns the element's metadata.
     */
    val metadata: FileSystemElementMetadata

    /**
     * Returns the element's path.
     */
    val absolutePath: String

    /**
     * View the element as a regular file.
     */
    fun toFile(): RegularFile

    /**
     * View the element as a directory.
     */
    fun toDir(): Directory
}