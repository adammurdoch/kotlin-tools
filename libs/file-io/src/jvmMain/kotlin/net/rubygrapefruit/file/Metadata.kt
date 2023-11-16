package net.rubygrapefruit.file

import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributeView
import kotlin.io.path.pathString

internal fun metadata(path: Path): Result<ElementMetadata> {
    if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
        return MissingEntry(path.pathString)
    }
    return Success(metadataOfExistingFile(path))
}

internal fun metadataOfExistingFile(path: Path): ElementMetadata {
    val attributes = Files.getFileAttributeView(path, BasicFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS).readAttributes()
    return when {
        attributes.isRegularFile -> RegularFileMetadata(attributes.size().toULong())
        attributes.isDirectory -> DirectoryMetadata
        attributes.isSymbolicLink -> SymlinkMetadata
        else -> OtherMetadata
    }
}
