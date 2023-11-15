package net.rubygrapefruit.file

internal class AbsolutePath(override val absolutePath: String) : ElementPath {
    override fun toString(): String {
        return absolutePath
    }

    override val name: String
        get() = absolutePath.substringAfterLast("/")
}