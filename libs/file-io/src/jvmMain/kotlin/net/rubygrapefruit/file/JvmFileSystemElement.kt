package net.rubygrapefruit.file

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import net.rubygrapefruit.io.Resource
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.*
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE
import java.nio.file.attribute.PosixFileAttributeView
import java.util.stream.Collectors
import kotlin.io.path.deleteExisting
import kotlin.io.path.pathString

internal open class JvmFileSystemElement(protected val delegate: Path) : AbstractFileSystemElement() {
    init {
        require(delegate.isAbsolute)
    }

    override val path = JvmElementPath(delegate)

    override val parent: JvmDirectory?
        get() {
            val parent = delegate.parent
            return if (parent != null) {
                JvmDirectory(parent)
            } else {
                null
            }
        }

    fun toPath(): Path = delegate

    override fun toFile(): RegularFile {
        return JvmRegularFile(delegate)
    }

    override fun toDir(): Directory {
        return JvmDirectory(delegate)
    }

    override fun toSymLink(): SymLink {
        return JvmSymlink(delegate)
    }

    override fun metadata(): Result<ElementMetadata> {
        return metadata(delegate)
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata().map { JvmSnapshot(path, it) }
    }

    override fun posixPermissions(): Result<PosixPermissions> {
        val view = posixFileAttributeView()
        return if (view == null) {
            readPermissionNotSupported(absolutePath)
        } else {
            try {
                val attributes = view.readAttributes()
                Success(attributes.permissions().permissions())
            } catch (_: NoSuchFileException) {
                readPermissionOnMissingElement(absolutePath)
            } catch (e: Exception) {
                readPermission(absolutePath, cause = e)
            }
        }
    }

    override fun setPermissions(permissions: PosixPermissions) {
        val view = posixFileAttributeView()
        if (view == null) {
            throw setPermissionsNotSupported(absolutePath)
        }
        try {
            view.setPermissions(permissions.permSet())
        } catch (_: NoSuchFileException) {
            throw setPermissionsOnMissingElement(absolutePath)
        } catch (e: IOException) {
            if (metadata().symlink) {
                throw setPermissionsNotSupported(absolutePath)
            } else {
                throw setPermissions(absolutePath, cause = e)
            }
        }
    }

    override fun supports(capability: FileSystemCapability): Boolean {
        return when (capability) {
            FileSystemCapability.PosixPermissions -> posixFileAttributeView() != null
            FileSystemCapability.SetSymLinkPosixPermissions -> false
        }
    }

    private fun posixFileAttributeView(): PosixFileAttributeView? =
        Files.getFileAttributeView(delegate, PosixFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS)
}

internal class JvmRegularFile(path: Path) : JvmFileSystemElement(path), RegularFile {
    override val parent: JvmDirectory
        get() = super.parent!!

    override fun toFile(): RegularFile {
        return this
    }

    override fun delete() {
        delete(this) { Files.deleteIfExists(it.delegate) }
    }

    override fun openContent(): Resource<FileContent> {
        return Resource(doOpenContent()) { it.close() }
    }

    override fun <T> withContent(action: (FileContent) -> T): T {
        return doOpenContent().use { content ->
            action((content))
        }
    }

    private fun doOpenContent(): JvmFileContent {
        val file = try {
            RandomAccessFile(delegate.toFile(), "rw")
        } catch (e: Exception) {
            throw openFile(this, cause = e)
        }
        return JvmFileContent(this, file)
    }

    override fun <T> write(action: (Sink) -> T): T {
        val outputStream = try {
            Files.newOutputStream(delegate, NOFOLLOW_LINKS, CREATE, TRUNCATE_EXISTING, WRITE)
        } catch (e: Exception) {
            throw writeToFile(this, cause = e)
        }
        return outputStream.use { stream ->
            val sink = OutputStreamBackedRawSink(stream).buffered()
            val result = action(sink)
            sink.flush()
            result
        }
    }

    override fun <T> read(action: (Source) -> T): T {
        val inputStream = try {
            Files.newInputStream(delegate, NOFOLLOW_LINKS)
        } catch (e: Exception) {
            throw readFile(this, cause = e)
        }
        return inputStream.use { stream ->
            val source = InputStreamBackedRawSource(this, stream).buffered()
            action(source)
        }
    }
}

internal class JvmDirectory(path: Path) : JvmFileSystemElement(path), Directory {

    override fun toDir(): Directory {
        return this
    }

    override fun file(name: String): RegularFile {
        return JvmRegularFile(resolve(name))
    }

    override fun dir(name: String): Directory {
        return JvmDirectory(resolve(name))
    }

    override fun symLink(name: String): SymLink {
        return JvmSymlink(resolve(name))
    }

    private fun resolve(name: String): Path = delegate.resolve(name).normalize()

    override fun deleteRecursively() {
        deleteRecursively(this) { entry ->
            if (entry is JvmDirectoryEntry) {
                try {
                    entry.delegate.deleteExisting()
                } catch (e: Exception) {
                    throw deleteDirectory(entry.toDir(), cause = e)
                }
            } else {
                Paths.get(entry.absolutePath).deleteExisting()
            }
        }
    }

    override fun createTemporaryDirectory(): Directory {
        return JvmDirectory(Files.createTempDirectory(this.delegate, "dir"))
    }

    override fun createDirectories() {
        try {
            Files.createDirectories(delegate)
        } catch (e: FileAlreadyExistsException) {
            throw createDirectoryThatExistsAndIsNotADir(delegate.pathString, e)
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
                throw createDirectoryThatExistsAndIsNotADir(p.absolutePath, e)
            }
            // Nothing in the hierarchy exists, which is unexpected, so rethrow original failure
            throw createDirectory(delegate.pathString, e)
        }
    }

    override fun listEntries(): Result<List<DirectoryEntry>> {
        val stream = try {
            Files.list(delegate)
        } catch (e: NoSuchFileException) {
            return listDirectoryThatDoesNotExist(delegate.pathString, cause = e)
        } catch (_: AccessDeniedException) {
            return listDirectoryThatIsNotReadable(delegate.pathString)
        } catch (e: Exception) {
            return listDirectory(this, cause = e)
        }
        val entries = stream.map { JvmDirectoryEntry(it, metadataOfExistingFile(it).type) }.collect(Collectors.toList())
        return Success(entries)
    }

    override fun visitTopDown(visitor: (DirectoryEntry) -> Unit) {
        visitTopDown(this, visitor)
    }
}

internal class JvmSymlink(path: Path) : JvmFileSystemElement(path), SymLink {
    override val parent: JvmDirectory
        get() = super.parent!!

    override fun toSymLink(): SymLink {
        return this
    }

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

private class JvmDirectoryEntry(val delegate: Path, override val type: ElementType) : DirectoryEntry {
    override val path: JvmElementPath
        get() = JvmElementPath(delegate)

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata(delegate).map { JvmSnapshot(path, it) }
    }

    override fun toDir(): Directory {
        return JvmDirectory(delegate)
    }

    override fun toFile(): RegularFile {
        return JvmRegularFile(delegate)
    }

    override fun toSymLink(): SymLink {
        return JvmSymlink(delegate)
    }
}

internal class JvmSnapshot(override val path: JvmElementPath, override val metadata: ElementMetadata) : AbstractElementSnapshot() {
    override fun asRegularFile(): RegularFile {
        return JvmRegularFile(path.delegate)
    }

    override fun asDirectory(): Directory {
        return JvmDirectory(path.delegate)
    }

    override fun asSymLink(): SymLink {
        return JvmSymlink(path.delegate)
    }
}