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
     * Returns the parent of this path.
     */
    val parent: ElementPath?

    /**
     * Returns the path of this element, using file system separators and formatting.
     */
    val absolutePath: String

    /**
     * Resolves the given path relative to this path.
     */
    fun resolve(path: String): ElementPath

    /**
     * Returns a snapshot of the element, which contains the path of this element and a snapshot of its metadata. Does not follow symlinks.
     */
    fun snapshot(): Result<ElementSnapshot>
}