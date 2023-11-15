package net.rubygrapefruit.file

/**
 * An immutable snapshot of a file system element's path, type and basic metadata.
 */
interface ElementSnapshot : HasPath {
    /**
     * Returns the element's metadata.
     */
    val metadata: ElementMetadata
}