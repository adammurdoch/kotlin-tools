package net.rubygrapefruit.file

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
     * View the element as a regular file.
     */
    fun toFile(): RegularFile

    /**
     * View the element as a directory.
     */
    fun toDir(): Directory

    /**
     * View the element as a symlink.
     */
    fun toSymLink(): SymLink
}