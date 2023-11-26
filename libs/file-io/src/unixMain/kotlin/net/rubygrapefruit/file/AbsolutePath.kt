package net.rubygrapefruit.file

internal class AbsolutePath(override val absolutePath: String) : ElementPath {
    init {
        require(absolutePath.startsWith("/"))
    }

    override fun toString(): String {
        return absolutePath
    }

    override val name: String
        get() = absolutePath.substringAfterLast("/")

    override val parent: AbsolutePath?
        get() {
            return if (absolutePath == "/") {
                null
            } else {
                AbsolutePath(absolutePath.substringBeforeLast("/"))
            }
        }

    override fun snapshot(): Result<ElementSnapshot> {
        return stat(absolutePath).map { SnapshotImpl(this, it) }
    }
}