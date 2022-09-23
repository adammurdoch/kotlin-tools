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

    actual fun metadata(): FileSystemElementMetadata {
        return stat(path)
    }

    actual fun resolve(): FileResolveResult {
        return ResolveResultImpl(path, metadata())
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
        return RegularFile(resolveName(name))
    }

    actual fun dir(name: String): Directory {
        return Directory(resolveName(name))
    }

    actual fun createTemporaryDirectory(): Directory {
        return createTempDir(this)
    }

    actual fun createDirectories() {
        createDir(this)
    }

    actual fun resolve(name: String): FileResolveResult {
        val path = resolveName(name)
        return ResolveResultImpl(path, stat(path))
    }

    private fun resolveName(name: String): String {
        if (name.startsWith("/")) {
            return name
        } else if (name == ".") {
            return path
        } else if (name.startsWith("./")) {
            return resolveName(name.substring(2))
        } else if (name == "..") {
            return parent!!.absolutePath
        } else if (name.startsWith("../")) {
            return parent!!.resolveName(name.substring(3))
        } else {
            return "$path/$name"
        }
    }
}

private class ResolveResultImpl(override val absolutePath: String, override val metadata: FileSystemElementMetadata) : AbstractFileResolveResult() {
    override fun asRegularFile(): RegularFile {
        return RegularFile(absolutePath)
    }

    override fun asDirectory(): Directory {
        return Directory(absolutePath)
    }
}

internal expect fun stat(file: String): FileSystemElementMetadata

internal expect fun getUserHomeDir(): Directory

internal expect fun getCurrentDir(): Directory

internal expect fun createTempDir(baseDir: Directory): Directory

internal expect fun createDir(dir: Directory)

internal expect fun writeToFile(file: RegularFile, text: String)
