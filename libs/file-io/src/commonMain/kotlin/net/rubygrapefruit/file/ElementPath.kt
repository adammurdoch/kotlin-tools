package net.rubygrapefruit.file

/**
 * The absolute path of a file system element.
 */
interface ElementPath {
    /**
     * Returns the base name of this path.
     */
    val name: String

    /**
     * Returns the path of this element.
     */
    val absolutePath: String

    /**
     * Returns a snapshot of the element, which contains the path of this element and a snapshot of its metadata. Does not follow symlinks.
     */
    fun snapshot(): Result<ElementSnapshot>
}