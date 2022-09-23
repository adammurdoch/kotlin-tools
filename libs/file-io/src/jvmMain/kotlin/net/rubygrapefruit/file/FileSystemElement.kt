package net.rubygrapefruit.file

import java.nio.file.Files
import java.nio.file.Path
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
}