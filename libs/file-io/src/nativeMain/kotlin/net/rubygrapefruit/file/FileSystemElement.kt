package net.rubygrapefruit.file

actual sealed interface FileSystemElement {
    actual val parent: Directory?

    actual val name: String

    actual val absolutePath: String

    actual fun metadata(): FileSystemElementMetadata

    actual fun resolve(): FileResolveResult
}

internal abstract class PathFileSystemElement(internal val path: String) : FileSystemElement {
    init {
        require(path.startsWith("/"))
    }

    override val name: String
        get() = path.substringAfterLast("/")

    override val absolutePath: String
        get() = path

    override fun toString(): String {
        return path
    }
}
