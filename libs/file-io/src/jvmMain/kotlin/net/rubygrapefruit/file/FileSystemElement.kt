package net.rubygrapefruit.file

import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributeView
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

    actual fun metadata(): ElementMetadata

    actual fun resolve(): ElementResolveResult
}

internal sealed class JvmFileSystemElement(protected val path: Path) : FileSystemElement {
    init {
        require(path.isAbsolute)
    }

    override fun toString(): String {
        return path.pathString
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

    /**
     * Returns this element as a JVM `File`.
     */
    override fun toFile(): File = path.toFile()

    override fun metadata(): ElementMetadata {
        return metadata(path)
    }

    protected fun metadata(path: Path): ElementMetadata {
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            return MissingEntryMetadata
        }
        return metadataOfExistingFile(path)
    }

    protected fun metadataOfExistingFile(path: Path): ExistingElementMetadata {
        val attributes = Files.getFileAttributeView(path, BasicFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS).readAttributes()
        return when {
            attributes.isRegularFile -> RegularFileMetadata(attributes.size().toULong())
            attributes.isDirectory -> DirectoryMetadata
            attributes.isSymbolicLink -> SymlinkMetadata
            else -> OtherMetadata
        }
    }

    override fun resolve(): ElementResolveResult {
        return ResolveResultImpl(path, metadata())
    }
}

internal class JvmRegularFile(path: Path) : JvmFileSystemElement(path), RegularFile {
    override fun writeText(text: String) {
        try {
            Files.writeString(path, text, Charsets.UTF_8)
        } catch (e: IOException) {
            throw writeToFile(this, e)
        }
    }

    override fun readText(): String {
        return Files.readString(path, Charsets.UTF_8)
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
                when (p.metadata()) {
                    // Found a directory - should have been able to create dir so rethrow original failure
                    DirectoryMetadata -> throw createDirectory(path.pathString, e)
                    // Keep looking
                    MissingEntryMetadata -> p = p.parent
                    // Found something else - fail
                    else -> throw directoryExistsAndIsNotADir(p.absolutePath, e)
                }
            }
            // Nothing in the hierarchy exists, which is unexpected, so rethrow original failure
            throw createDirectory(path.pathString, e)
        }
    }

    override fun resolve(name: String): ElementResolveResult {
        val path = path.resolve(name)
        return ResolveResultImpl(path, metadata(path))
    }

    override fun listEntries(): DirectoryEntries {
        val stream = try {
            Files.list(path)
        } catch (e: NoSuchFileException) {
            return MissingDirectoryEntries
        }
        val entries = stream.map { DirectoryEntryImpl(it, metadataOfExistingFile(it).type) }.toList()
        return ExistingDirectoryEntries(entries)
    }
}

internal class JvmSymlink(path: Path) : JvmFileSystemElement(path), SymLink {
    override fun readSymLink(): String {
        return Files.readSymbolicLink(path).pathString
    }

    override fun writeSymLink(target: String) {
        if (metadata() is SymlinkMetadata) {
            Files.delete(this.path)
        }
        Files.createSymbolicLink(this.path, Path.of(target))
    }
}

private class DirectoryEntryImpl(private val path: Path, override val type: ElementType) : DirectoryEntry {
    override val name: String
        get() = path.name
}

private class ResolveResultImpl(private val path: Path, override val metadata: ElementMetadata) : AbstractElementResolveResult() {
    override val absolutePath: String
        get() = path.pathString

    override fun asRegularFile(): RegularFile {
        return JvmRegularFile(path)
    }

    override fun asDirectory(): Directory {
        return JvmDirectory(path)
    }
}