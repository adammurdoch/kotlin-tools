package net.rubygrapefruit.file

import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFileAttributeView
import java.util.concurrent.TimeUnit
import kotlin.io.path.pathString

internal fun metadata(path: Path): Result<ElementMetadata> {
    if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
        return MissingEntry(path.pathString)
    }
    return Success(metadataOfExistingFile(path))
}

internal fun metadataOfExistingFile(path: Path): ElementMetadata {
    val posixView = Files.getFileAttributeView(path, PosixFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS)
    return if (posixView == null) {
        val basicAttributes = Files.getFileAttributeView(path, BasicFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS).readAttributes()
        fromAttributes(basicAttributes, null)
    } else {
        val posixAttributes = posixView.readAttributes()
        val permissions = posixAttributes.permissions().permissions()
        fromAttributes(posixAttributes, permissions)
    }
}

private fun fromAttributes(
    attributes: BasicFileAttributes,
    permissions: PosixPermissions?
): ElementMetadata {
    val lastModified = Timestamp(attributes.lastModifiedTime().to(TimeUnit.NANOSECONDS))
    return when {
        attributes.isRegularFile -> RegularFileMetadata(attributes.size(), lastModified, permissions)
        attributes.isDirectory -> DirectoryMetadata(lastModified, permissions)
        attributes.isSymbolicLink -> SymlinkMetadata(lastModified, permissions)
        else -> OtherMetadata(lastModified, permissions)
    }
}
