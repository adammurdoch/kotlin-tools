package net.rubygrapefruit.file

internal abstract class NativeFileSystemElement(path: String) : AbstractFileSystemElement() {
    init {
        require(path.startsWith("/"))
    }

    override val path = AbsolutePath(path)
}
