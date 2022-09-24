package net.rubygrapefruit.file

import java.io.File
import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributeView
import kotlin.io.path.name
import kotlin.io.path.pathString

actual sealed class FileSystemElement(protected val path: Path) {
    init {
        require(path.isAbsolute)
    }

    override fun toString(): String {
        return path.pathString
    }

    actual val parent: Directory?
        get() {
            val parent = path.parent
            return if (parent != null) {
                Directory(parent)
            } else {
                null
            }
        }

    actual val name: String
        get() = path.name

    actual val absolutePath: String
        get() = path.pathString

    /**
     * Returns this element as a JVM `Path`.
     */
    fun toPath(): Path = path

    /**
     * Returns this element as a JVM `File`.
     */
    fun toFile(): File = path.toFile()

    actual fun metadata(): FileSystemElementMetadata {
        return metadata(path)
    }

    protected fun metadata(path: Path): FileSystemElementMetadata {
        if (!Files.exists(path)) {
            return MissingEntryMetadata
        }
        val attributes = Files.getFileAttributeView(path, BasicFileAttributeView::class.java).readAttributes()
        return when {
            attributes.isRegularFile -> RegularFileMetadata(attributes.size().toULong())
            attributes.isDirectory -> DirectoryMetadata
            attributes.isSymbolicLink -> SymlinkMetadata
            else -> OtherMetadata
        }
    }

    actual fun resolve(): FileResolveResult {
        return ResolveResultImpl(path, metadata())
    }
}

actual class RegularFile internal constructor(path: Path) : FileSystemElement(path) {
    actual fun writeText(text: String) {
        try {
            Files.writeString(path, text, Charsets.UTF_8)
        } catch (e: IOException) {
            throw writeToFile(this, e)
        }
    }

    actual fun readText(): String {
        return Files.readString(path, Charsets.UTF_8)
    }
}

actual class Directory internal constructor(path: Path) : FileSystemElement(path) {
    actual companion object {
        actual val current: Directory
            get() = Directory(Path.of(".").toAbsolutePath().normalize())

        actual val userHome: Directory
            get() = Directory(Path.of(System.getProperty("user.home")).toAbsolutePath().normalize())
    }

    actual fun file(name: String): RegularFile {
        return RegularFile(path.resolve(name))
    }

    actual fun dir(name: String): Directory {
        return Directory(path.resolve(name))
    }

    actual fun createTemporaryDirectory(): Directory {
        return Directory(Files.createTempDirectory(this.path, "dir"))
    }

    actual fun createDirectories() {
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

    actual fun resolve(name: String): FileResolveResult {
        val path = path.resolve(name)
        return ResolveResultImpl(path, metadata(path))
    }
}

internal class ResolveResultImpl(private val path: Path, override val metadata: FileSystemElementMetadata) : AbstractFileResolveResult() {
    override val absolutePath: String
        get() = path.pathString

    override fun asRegularFile(): RegularFile {
        return RegularFile(path)
    }

    override fun asDirectory(): Directory {
        return Directory(path)
    }
}