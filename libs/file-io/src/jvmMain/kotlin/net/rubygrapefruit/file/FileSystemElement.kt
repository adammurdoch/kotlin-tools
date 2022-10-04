package net.rubygrapefruit.file

import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.PosixFileAttributeView
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.streams.toList

actual sealed interface FileSystemElement {
    /**
     * Returns this element as a JVM `Path`.
     */
    fun toPath(): Path

    /**
     * Returns this element as a JVM `File`.
     */
    fun toFile(): File

    actual val parent: Directory?

    actual val name: String

    actual val absolutePath: String

    actual fun metadata(): Result<ElementMetadata>

    actual fun snapshot(): Result<ElementSnapshot>

    actual fun posixPermissions(): Result<PosixPermissions>

    actual fun setPermissions(permissions: PosixPermissions)

    actual fun supports(capability: FileSystemCapability): Boolean
}

internal open class JvmFileSystemElement(protected val path: Path) : AbstractFileSystemElement() {
    init {
        require(path.isAbsolute)
    }

    override val parent: Directory?
        get() {
            val parent = path.parent
            return if (parent != null) {
                JvmDirectory(parent)
            } else {
                null
            }
        }

    override val name: String
        get() = path.name

    override val absolutePath: String
        get() = path.pathString

    override fun toPath(): Path = path

    override fun toFile(): File = path.toFile()

    override fun metadata(): Result<ElementMetadata> {
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            return MissingEntry(absolutePath)
        }
        return Success(metadataOfExistingFile(path))
    }

    protected fun metadataOfExistingFile(path: Path): ElementMetadata {
        val attributes = Files.getFileAttributeView(path, BasicFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS).readAttributes()
        return when {
            attributes.isRegularFile -> RegularFileMetadata(attributes.size().toULong())
            attributes.isDirectory -> DirectoryMetadata
            attributes.isSymbolicLink -> SymlinkMetadata
            else -> OtherMetadata
        }
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata().map { SnapshotImpl(path, it) }
    }

    override fun posixPermissions(): Result<PosixPermissions> {
        val attributes = Files.getFileAttributeView(path, PosixFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS).readAttributes()
        return Success(attributes.permissions().permissions())
    }

    override fun setPermissions(permissions: PosixPermissions) {
        Files.getFileAttributeView(path, PosixFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS).setPermissions(permissions.permSet())
    }

    override fun supports(capability: FileSystemCapability): Boolean {
        return when (capability) {
            FileSystemCapability.SetSymLinkPosixPermissions -> false
        }
    }
}

internal class JvmRegularFile(path: Path) : JvmFileSystemElement(path), RegularFile {
    override fun delete() {
        Files.deleteIfExists(path)
    }

    override fun writeText(text: String) {
        try {
            Files.writeString(path, text, Charsets.UTF_8)
        } catch (e: IOException) {
            throw writeToFile(this, e)
        }
    }

    override fun readText(): Result<String> {
        return Success(Files.readString(path, Charsets.UTF_8))
    }
}

internal class JvmDirectory(path: Path) : JvmFileSystemElement(path), Directory {
    override fun file(name: String): RegularFile {
        return JvmRegularFile(path.resolve(name))
    }

    override fun dir(name: String): Directory {
        return JvmDirectory(path.resolve(name))
    }

    override fun symLink(name: String): SymLink {
        return JvmSymlink(path.resolve(name))
    }

    override fun deleteRecursively() {
        path.toFile().deleteRecursively()
    }

    override fun createTemporaryDirectory(): Directory {
        return JvmDirectory(Files.createTempDirectory(this.path, "dir"))
    }

    override fun createDirectories() {
        try {
            Files.createDirectories(path)
        } catch (e: FileAlreadyExistsException) {
            throw directoryExistsAndIsNotADir(path.pathString, e)
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
                    throw createDirectory(path.pathString, e)
                }
                // Found something else - fail
                throw directoryExistsAndIsNotADir(p.absolutePath, e)
            }
            // Nothing in the hierarchy exists, which is unexpected, so rethrow original failure
            throw createDirectory(path.pathString, e)
        }
    }

    override fun resolve(name: String): FileSystemElement {
        val path = path.resolve(name)
        return JvmRegularFile(path)
    }

    override fun listEntries(): Result<List<DirectoryEntry>> {
        val stream = try {
            Files.list(path)
        } catch (e: NoSuchFileException) {
            return MissingEntry(path.pathString, e)
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
        return Success(Files.readSymbolicLink(path).pathString)
    }

    override fun writeSymLink(target: String) {
        val metadata = metadata()
        if (metadata is Success && metadata.get() is SymlinkMetadata) {
            Files.delete(this.path)
        }
        Files.createSymbolicLink(this.path, Path.of(target))
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