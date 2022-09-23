package net.rubygrapefruit.file

actual sealed class FileSystemElement(internal val path: String) {
    init {
        require(path.startsWith("/"))
    }

    actual val parent: Directory?
        get() {
            return if (path == "/") {
                null
            } else {
                Directory(path.substringBeforeLast("/"))
            }
        }

    actual val name: String
        get() = path.substringAfterLast("/")

    actual val absolutePath: String
        get() = path

    override fun toString(): String {
        return path
    }
}

actual class RegularFile internal constructor(path: String) : FileSystemElement(path) {
    actual fun writeText(text: String) {
        writeToFile(this, text)
    }
}

actual class Directory internal constructor(path: String) : FileSystemElement(path) {
    actual companion object {
        actual val current: Directory
            get() = getCurrentDir()

        actual val userHome: Directory
            get() = getUserHomeDir()
    }

    actual fun file(name: String): RegularFile {
        return RegularFile("$path/$name")
    }

    actual fun dir(name: String): Directory {
        return Directory("$path/$name")
    }

    actual fun createTemporaryDirectory(): Directory {
        return createTempDir(this)
    }

    actual fun createDirectories() {
        createDir(this)
    }
}

internal expect fun getUserHomeDir(): Directory

internal expect fun getCurrentDir(): Directory

internal expect fun createTempDir(baseDir: Directory): Directory

internal expect fun createDir(dir: Directory)

internal expect fun writeToFile(file: RegularFile, text: String)
