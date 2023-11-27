package net.rubygrapefruit.file

internal class WinPath(override val absolutePath: String) : ElementPath {
    init {
        require(isAbsolute(absolutePath))
    }

    override val name: String
        get() = TODO("Not yet implemented")

    override val parent: WinPath?
        get() {
            return if (absolutePath.length == 3) {
                null
            } else {
                WinPath(absolutePath.substringBeforeLast("\\"))
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