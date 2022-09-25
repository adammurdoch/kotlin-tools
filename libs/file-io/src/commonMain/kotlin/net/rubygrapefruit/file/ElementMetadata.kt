package net.rubygrapefruit.file

/**
 * An immutable snapshot of some basic metadata about a file system element.
 */
sealed class ElementMetadata

object MissingEntryMetadata : ElementMetadata()

object UnreadableEntryMetadata : ElementMetadata()

sealed class ExistingElementMetadata : ElementMetadata() {
    abstract val type: ElementType
}

object DirectoryMetadata : ExistingElementMetadata() {
    override val type: ElementType
        get() = ElementType.Directory
}

object SymlinkMetadata : ExistingElementMetadata() {
    override val type: ElementType
        get() = ElementType.SymLink
}

object OtherMetadata : ExistingElementMetadata() {
    override val type: ElementType
        get() = ElementType.Other
}

data class RegularFileMetadata(val size: ULong) : ExistingElementMetadata() {
    override val type: ElementType
        get() = ElementType.RegularFile
}
