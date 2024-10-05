package net.rubygrapefruit.file

internal data class WinPath(override val absolutePath: String) : StringBackedAbsolutePath() {
    init {
        require(isAbsolute(absolutePath))
    }

    override val separator: Char
        get() = '\\'

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

    override fun toString(): String {
        return absolutePath
    }

    override fun child(name: String): StringBackedAbsolutePath {
        return if (absolutePath.length == 3) {
            WinPath("$absolutePath$name")
        } else {
            WinPath("$absolutePath\\$name")
        }
    }

    override fun resolve(path: String): StringBackedAbsolutePath {
        val normalized = path.replace("/", "\\")
        return if (isAbsolute(normalized)) {
            val root = WinPath("${normalized[0]}:\\")
            resolve(root, path.drop(3))
        } else {
            resolve(this, normalized)
        }
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata(absolutePath).map { WinElementSnapshot(this, it) }
    }

    override fun isAbsolute(path: String): Boolean {
        return path.length >= 3 && path[0].isLetter() && path[1] == ':' && path[2] == '\\'
    }
}