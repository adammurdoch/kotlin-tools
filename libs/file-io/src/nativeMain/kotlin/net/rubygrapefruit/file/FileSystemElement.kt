package net.rubygrapefruit.file

internal abstract class PathFileSystemElement(internal val path: String) : AbstractFileSystemElement() {
    init {
        require(path.startsWith("/"))
    }

    override val name: String
        get() = path.substringAfterLast("/")

    override val absolutePath: String
        get() = path
}
