package net.rubygrapefruit.file

actual sealed interface FileSystemElement {
    actual val parent: Directory?

    actual val name: String

    actual val absolutePath: String

    actual fun metadata(): FileSystemElementMetadata

    actual fun resolve(): FileResolveResult
}

internal sealed class PathFileSystemElement(internal val path: String) : FileSystemElement {
    init {
        require(path.startsWith("/"))
    }

    override val parent: Directory?
        get() {
            return if (path == "/") {
                null
            } else {
                NativeDirectory(path.substringBeforeLast("/"))
            }
        }

    override val name: String
        get() = path.substringAfterLast("/")

    override val absolutePath: String
        get() = path

    override fun toString(): String {
        return path
    }

    override fun metadata(): FileSystemElementMetadata {
        return stat(path)
    }

    override fun resolve(): FileResolveResult {
        return ResolveResultImpl(path, metadata())
    }
}

internal class NativeRegularFile internal constructor(path: String) : PathFileSystemElement(path), RegularFile {
    override fun writeText(text: String) {
        writeToFile(this, text)
    }

    override fun readText(): String {
        return readFromFile(this)
    }
}

internal class NativeDirectory internal constructor(path: String) : PathFileSystemElement(path), Directory {
    override fun file(name: String): RegularFile {
        return NativeRegularFile(resolveName(name))
    }

    override fun dir(name: String): Directory {
        return NativeDirectory(resolveName(name))
    }

    override fun createTemporaryDirectory(): Directory {
        return createTempDir(this)
    }

    override fun createDirectories() {
        val parent = parent
        if (parent != null) {
            if (parent.metadata() != DirectoryMetadata) {
                // Error handling will deal with parent being a file, etc
                parent.createDirectories()
            }
        }
        createDir(this)
    }

    override fun resolve(name: String): FileResolveResult {
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
            return (parent as NativeDirectory).resolveName(name.substring(3))
        } else {
            return "$path/$name"
        }
    }
}

private class ResolveResultImpl(override val absolutePath: String, override val metadata: FileSystemElementMetadata) : AbstractFileResolveResult() {
    override fun asRegularFile(): RegularFile {
        return NativeRegularFile(absolutePath)
    }

    override fun asDirectory(): Directory {
        return NativeDirectory(absolutePath)
    }
}

internal expect fun stat(file: String): FileSystemElementMetadata

internal expect fun getUserHomeDir(): Directory

internal expect fun getCurrentDir(): Directory

internal expect fun createTempDir(baseDir: NativeDirectory): Directory

internal expect fun createDir(dir: NativeDirectory)

internal expect fun writeToFile(file: NativeRegularFile, text: String)

internal expect fun readFromFile(file: NativeRegularFile): String
