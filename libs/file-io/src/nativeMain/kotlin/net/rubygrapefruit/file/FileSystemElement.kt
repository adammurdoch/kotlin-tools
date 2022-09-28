package net.rubygrapefruit.file

actual sealed interface FileSystemElement {
    actual val parent: Directory?

    actual val name: String

    actual val absolutePath: String

    actual fun metadata(): Result<ElementMetadata>

    actual fun snapshot(): Result<ElementSnapshot>

    actual fun posixPermissions(): Result<PosixPermissions>

    actual fun setPermissions(permissions: PosixPermissions)
}

internal abstract class PathFileSystemElement(internal val path: String) : AbstractFileSystemElement() {
    init {
        require(path.startsWith("/"))
    }

    override val name: String
        get() = path.substringAfterLast("/")

    override val absolutePath: String
        get() = path
}
