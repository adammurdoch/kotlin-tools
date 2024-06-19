package net.rubygrapefruit.file

internal data class AbsolutePath(override val absolutePath: String) : ElementPath {
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
                val parentPath = absolutePath.substringBeforeLast("/")
                return if (parentPath.length == 0) {
                    AbsolutePath("/")
                } else {
                    AbsolutePath(parentPath)
                }
            }
        }

    override fun resolve(path: String): AbsolutePath {
        return if (path.startsWith("/")) {
            AbsolutePath(path)
        } else if (path == ".") {
            this
        } else if (path.startsWith("./")) {
            resolve(path.substring(2))
        } else if (path == "..") {
            parent!!
        } else if (path.startsWith("../")) {
            parent!!.resolve(path.substring(3))
        } else {
            AbsolutePath("${absolutePath}/$path")
        }
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata(absolutePath).map { UnixSnapshot(this, it) }
    }
}