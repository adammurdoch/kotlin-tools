package net.rubygrapefruit.file

import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.PosixFileAttributeView
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.streams.toList

fun FileSystemElement.toPath(): Path {
    return (this as JvmFileSystemElement).toPath()
}

fun FileSystemElement.toFile(): File {
    return (this as JvmFileSystemElement).toFile()
}

internal open class JvmFileSystemElement(protected val delegate: Path) : AbstractFileSystemElement() {
    init {
        require(delegate.isAbsolute)
    }

    override val path: ElementPath = JvmElementPath(delegate)

    override val parent: Directory?
        get() {
            val parent = delegate.parent
            return if (parent != null) {
                JvmDirectory(parent)
            } else {
                null
            }
        }

    fun toPath(): Path = delegate

    fun toFile(): File = delegate.toFile()

    override fun metadata(): Result<ElementMetadata> {
        if (!Files.exists(delegate, LinkOption.NOFOLLOW_LINKS)) {
            return MissingEntry(absolutePath)
        }
        return Success(metadataOfExistingFile(delegate))
    }

    protected fun metadataOfExistingFile(path: Path): ElementMetadata {
        val attributes = Files.getFileAttributeView(path, BasicFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS)
            .readAttributes()
        return when {
            attributes.isRegularFile -> RegularFileMetadata(attributes.size().toULong())
            attributes.isDirectory -> DirectoryMetadata
            attributes.isSymbolicLink -> SymlinkMetadata
            else -> OtherMetadata
        }
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata().map { SnapshotImpl(delegate, it) }
    }

    override fun posixPermissions(): Result<PosixPermissions> {
        val attributes = Files.getFileAttributeView(delegate, PosixFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS)
            .readAttributes()
        return Success(attributes.permissions().permissions())
    }

    override fun setPermissions(permissions: PosixPermissions) {
        Files.getFileAttributeView(delegate, PosixFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS)
            .setPermissions(permissions.permSet())
    }

    override fun supports(capability: FileSystemCapability): Boolean {
        return when (capability) {
            FileSystemCapability.SetSymLinkPosixPermissions -> false
        }
    }
}

internal class JvmRegularFile(path: Path) : JvmFileSystemElement(path), RegularFile {
    override fun delete() {
        Files.deleteIfExists(delegate)
    }

    override fun writeText(text: String) {
        try {
            Files.writeString(delegate, text, Charsets.UTF_8)
        } catch (e: IOException) {
            throw writeToFile(this, e)
        }
    }

    override fun readText(): Result<String> {
        return Success(Files.readString(delegate, Charsets.UTF_8))
    }
}

internal class JvmDirectory(path: Path) : JvmFileSystemElement(path), Directory {
    override fun file(name: String): RegularFile {
        return JvmRegularFile(delegate.resolve(name))
    }

    override fun dir(name: String): Directory {
        return JvmDirectory(delegate.resolve(name))
    }

    override fun symLink(name: String): SymLink {
        return JvmSymlink(delegate.resolve(name))
    }

    override fun deleteRecursively() {
        delegate.toFile().deleteRecursively()
    }

    override fun createTemporaryDirectory(): Directory {
        return JvmDirectory(Files.createTempDirectory(this.delegate, "dir"))
    }

    override fun createDirectories() {
        try {
            Files.createDirectories(delegate)
        } catch (e: FileAlreadyExistsException) {
            throw directoryExistsAndIsNotADir(delegate.pathString, e)
        } catch (e: IOException) {
            var p = parent
            while (p != null) {
                val metadata = p.metadata()
                if (metadata is MissingEntry) {
                    // Keep looking
                    p = p.parent
                    continue
                }
                if (metadata.directory) {
                    // Found a directory - should have been able to create dir so rethrow original failure
                    throw createDirectory(delegate.pathString, e)
                }
                // Found something else - fail
                throw directoryExistsAndIsNotADir(p.absolutePath, e)
            }
            // Nothing in the hierarchy exists, which is unexpected, so rethrow original failure
            throw createDirectory(delegate.pathString, e)
        }
    }

    override fun resolve(name: String): FileSystemElement {
        val path = delegate.resolve(name)
        return JvmRegularFile(path)
    }

    override fun listEntries(): Result<List<DirectoryEntry>> {
        val stream = try {
            Files.list(delegate)
        } catch (e: NoSuchFileException) {
            return MissingEntry(delegate.pathString, e)
        }
        val entries = stream.map { DirectoryEntryImpl(it, metadataOfExistingFile(it).type) }.toList()
        return Success(entries)
    }

    override fun visitTopDown(visitor: (DirectoryEntry) -> Unit) {
        visitTopDown(this, visitor)
    }
}

internal class JvmSymlink(path: Path) : JvmFileSystemElement(path), SymLink {
    override fun readSymLink(): Result<String> {
        return Success(Files.readSymbolicLink(delegate).pathString)
    }

    override fun writeSymLink(target: String) {
        val metadata = metadata()
        if (metadata is Success && metadata.get() is SymlinkMetadata) {
            Files.delete(this.delegate)
        }
        Files.createSymbolicLink(this.delegate, Path.of(target))
    }
}

private class DirectoryEntryImpl(private val path: Path, override val type: ElementType) : DirectoryEntry {
    override val name: String
        get() = path.name

    override fun toDir(): Directory {
        return JvmDirectory(path)
    }

    override fun toElement(): FileSystemElement {
        return JvmFileSystemElement(path)
    }
}

private class SnapshotImpl(private val path: Path, override val metadata: ElementMetadata) : AbstractElementSnapshot() {
    override val absolutePath: String
        get() = path.pathString

    override fun asRegularFile(): RegularFile {
        return JvmRegularFile(path)
    }

    override fun asDirectory(): Directory {
        return JvmDirectory(path)
    }

    override fun asSymLink(): SymLink {
        return JvmSymlink(path)
    }
}