package net.rubygrapefruit.file

import java.io.File
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
                Directory(path)
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
    /**
     * Writes the given text to the file, using UTF-8 encoding.
     */
    actual fun writeText(text: String) {
        Files.writeString(path, text, Charsets.UTF_8)
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
        Files.createDirectories(path)
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