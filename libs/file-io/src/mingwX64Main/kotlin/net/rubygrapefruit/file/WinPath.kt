package net.rubygrapefruit.file

internal class WinPath(override val absolutePath: String) : ElementPath {
    init {
        require(isAbsolute(absolutePath)) { "'$absolutePath' is not absolute" }
    }

    override val name: String
        get() = absolutePath.substringAfterLast("\\")

    override val parent: WinPath?
        get() {
            return if (absolutePath.length == 3) {
                // Is root
                null
            } else {
                val parentPath = absolutePath.substringBeforeLast("\\")
                return if (parentPath.length == 2) {
                    // Direct child of the root
                    WinPath(absolutePath.substring(0, 3))
                } else {
                    WinPath(parentPath)
                }
            }
        }

    override fun resolve(path: String): WinPath {
        return if (isAbsolute(path)) {
            WinPath(path)
        } else if (path == ".") {
            this
        } else if (path.startsWith("./")) {
            resolve(path.substring(2))
        } else if (path == "..") {
            parent!!
        } else if (path.startsWith("../")) {
            parent!!.resolve(path.substring(3))
        } else {
            WinPath("${absolutePath}/$path")
        }
    }

    override fun snapshot(): Result<ElementSnapshot> {
        TODO("Not yet implemented")
    }

    private fun isAbsolute(path: String): Boolean {
        return path.length >= 3 && path[0].isLetter() && path[1] == ':' && path[2] == '\\'
    }
}