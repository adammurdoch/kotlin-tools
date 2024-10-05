package net.rubygrapefruit.file

internal data class UnixPath(override val absolutePath: String) : StringBackedAbsolutePath() {
    companion object {
        internal val ROOT = UnixPath("/")
    }

    init {
        require(isAbsolute(absolutePath))
    }

    override val separator: Char
        get() = '/'

    override val parent: UnixPath?
        get() {
            return if (absolutePath == "/") {
                null
            } else {
                val parentPath = absolutePath.substringBeforeLast("/")
                return if (parentPath.isEmpty()) {
                    UnixPath("/")
                } else {
                    UnixPath(parentPath)
                }
            }
        }

    override fun toString(): String {
        return absolutePath
    }

    override fun isAbsolute(path: String): Boolean {
        return path.startsWith('/')
    }

    override fun child(name: String): StringBackedAbsolutePath {
        return if (absolutePath == "/") {
            UnixPath("/$name")
        } else {
            UnixPath("$absolutePath/$name")
        }
    }

    override fun resolve(path: String): StringBackedAbsolutePath {
        return if (path == absolutePath || path == ".") {
            this
        } else if (path.startsWith("/")) {
            resolve(ROOT, path.drop(1))
        } else {
            resolve(this, path)
        }
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata(absolutePath).map { UnixSnapshot(this, it) }
    }
}