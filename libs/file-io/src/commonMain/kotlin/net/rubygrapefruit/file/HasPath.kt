package net.rubygrapefruit.file

/**
 * Represents a file system element.
 */
interface HasPath {
    /**
     * Returns the base name of this element.
     */
    val name: String
        get() = path.name

    /**
     * Returns the absolute path of this element.
     */
    val absolutePath: String
        get() = path.absolutePath

    /**
     * Returns the absolute path of this element.
     */
    val path: ElementPath

    /**
     * Returns a snapshot of the element, which contains the path of this element and a snapshot of its metadata. Does not follow symlinks.
     */
    fun snapshot(): Result<ElementSnapshot>

    /**
     * Views this element as a regular file. Does not check whether the file system element is a regular file.
     */
    fun toFile(): RegularFile

    /**
     * Views this element as a directory. Does not check whether the file system element is a directory file.
     */
    fun toDir(): Directory

    /**
     * Views this element as a symlink. Does not check whether the file system element is a symlink file.
     */
    fun toSymLink(): SymLink
}