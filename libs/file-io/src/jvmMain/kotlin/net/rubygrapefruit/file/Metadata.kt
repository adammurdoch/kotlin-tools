package net.rubygrapefruit.file

import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
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
    val attributes = Files.getFileAttributeView(path, PosixFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS).readAttributes()
    val lastModified = Timestamp(attributes.lastModifiedTime().to(TimeUnit.NANOSECONDS))
    val permissions = attributes.permissions().permissions()
    return when {
        attributes.isRegularFile -> RegularFileMetadata(attributes.size(), lastModified, permissions)
        attributes.isDirectory -> DirectoryMetadata(lastModified, permissions)
        attributes.isSymbolicLink -> SymlinkMetadata(lastModified, permissions)
        else -> OtherMetadata(lastModified, permissions)
    }
}
