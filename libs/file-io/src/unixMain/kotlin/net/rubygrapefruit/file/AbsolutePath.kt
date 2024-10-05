package net.rubygrapefruit.file

internal data class AbsolutePath(override val absolutePath: String) : StringBackedAbsolutePath() {
    companion object {
        internal val ROOT = AbsolutePath("/")
    }

    override val separator: Char
        get() = '/'

    override val parent: AbsolutePath?
        get() {
            return if (absolutePath == "/") {
                null
            } else {
                val parentPath = absolutePath.substringBeforeLast("/")
                return if (parentPath.isEmpty()) {
                    AbsolutePath("/")
                } else {
                    AbsolutePath(parentPath)
                }
            }
        }

    override fun isAbsolute(path: String): Boolean {
        return path.startsWith('/')
    }

    override fun child(name: String): StringBackedAbsolutePath {
        return if (absolutePath == "/") {
            AbsolutePath("/$name")
        } else {
            AbsolutePath("$absolutePath/$name")
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