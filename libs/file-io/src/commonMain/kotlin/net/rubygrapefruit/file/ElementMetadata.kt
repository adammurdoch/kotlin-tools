package net.rubygrapefruit.file

/**
 * An immutable snapshot of some basic metadata about a file system element.
 */
sealed class ElementMetadata {
    abstract val type: ElementType
}

object DirectoryMetadata : ElementMetadata() {
    override val type: ElementType
        get() = ElementType.Directory
}

object SymlinkMetadata : ElementMetadata() {
    override val type: ElementType
        get() = ElementType.SymLink
}

object OtherMetadata : ElementMetadata() {
    override val type: ElementType
        get() = ElementType.Other
}

data class RegularFileMetadata(val size: ULong) : ElementMetadata() {
    override val type: ElementType
        get() = ElementType.RegularFile
}

val Result<ElementMetadata>.missing: Boolean
    get() = this is MissingEntry

val Result<ElementMetadata>.directory: Boolean
    get() = getOrNull() is DirectoryMetadata

val Result<ElementMetadata>.regularFile: Boolean
    get() = getOrNull() is RegularFileMetadata
