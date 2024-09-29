package net.rubygrapefruit.file

internal data class AbsolutePath(override val absolutePath: String) : ElementPath {
    companion object {
        internal val ROOT = AbsolutePath("/")
    }

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
        return if (path == absolutePath || path == ".") {
            this
        } else if (path.startsWith("/")) {
            resolve(ROOT, path.drop(1))
        } else {
            resolve(this, path)
        }
    }

    private fun resolve(base: AbsolutePath, path: String): AbsolutePath {
        val elements = path.split("/").toMutableList()
        var current = base
        for (element in elements) {
            if (element == "" || element == ".") {
                continue
            }
            if (element == "..") {
                current = current.parent ?: ROOT
            } else if (current != ROOT) {
                current = AbsolutePath("$current/$element")
            } else {
                current = AbsolutePath("/$element")
            }
        }
        return current
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata(absolutePath).map { UnixSnapshot(this, it) }
    }
}