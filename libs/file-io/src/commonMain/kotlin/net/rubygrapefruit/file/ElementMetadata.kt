package net.rubygrapefruit.file

/**
 * An immutable snapshot of some basic metadata about a file system element.
 */
sealed interface ElementMetadata {
    val type: ElementType

    val lastModified: Timestamp

    /**
     * The POSIX permissions, if available.
     */
    val posixPermissions: PosixPermissions?
}

data class DirectoryMetadata(override val lastModified: Timestamp, override val posixPermissions: PosixPermissions?) : ElementMetadata {
    override val type: ElementType
        get() = ElementType.Directory
}

data class SymlinkMetadata(override val lastModified: Timestamp, override val posixPermissions: PosixPermissions?) : ElementMetadata {
    override val type: ElementType
        get() = ElementType.SymLink
}

data class OtherMetadata(override val lastModified: Timestamp, override val posixPermissions: PosixPermissions?) : ElementMetadata {
    override val type: ElementType
        get() = ElementType.Other
}

data class RegularFileMetadata(val size: Long, override val lastModified: Timestamp, override val posixPermissions: PosixPermissions?) : ElementMetadata {
    override val type: ElementType
        get() = ElementType.RegularFile
}

/**
 * Is the element missing?
 */
val Result<ElementMetadata>.missing: Boolean
    get() = this is MissingEntry

/**
 * Is the element a directory?
 */
val Result<ElementMetadata>.directory: Boolean
    get() = getOrNull() is DirectoryMetadata

/**
 * Is the element a regular file?
 */
val Result<ElementMetadata>.regularFile: Boolean
    get() = getOrNull() is RegularFileMetadata
