package net.rubygrapefruit.file

internal data class WinPath(override val absolutePath: String) : ElementPath {
    init {
        require(isAbsolute(absolutePath)) { "'$absolutePath' is not absolute" }
    }

    override fun toString(): String {
        return absolutePath
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
        val normalized = path.replace("/", "\\")
        return if (isAbsolute(normalized)) {
            WinPath(normalized)
        } else if (normalized == ".") {
            this
        } else if (normalized.startsWith("./")) {
            resolve(normalized.substring(2))
        } else if (normalized == "..") {
            parent!!
        } else if (normalized.startsWith("../")) {
            parent!!.resolve(normalized.substring(3))
        } else {
            WinPath("${absolutePath}\\$normalized")
        }
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata(absolutePath).map { WinElementSnapshot(this, it) }
    }

    private fun isAbsolute(path: String): Boolean {
        return path.length >= 3 && path[0].isLetter() && path[1] == ':' && path[2] == '\\'
    }
}