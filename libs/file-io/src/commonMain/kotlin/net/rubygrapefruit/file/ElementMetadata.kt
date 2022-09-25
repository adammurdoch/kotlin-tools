package net.rubygrapefruit.file

sealed class ElementMetadata {
    abstract val type: ElementType?
}

object MissingEntryMetadata : ElementMetadata() {
    override val type: ElementType?
        get() = null
}

object UnreadableEntryMetadata : ElementMetadata() {
    override val type: ElementType?
        get() = null
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
