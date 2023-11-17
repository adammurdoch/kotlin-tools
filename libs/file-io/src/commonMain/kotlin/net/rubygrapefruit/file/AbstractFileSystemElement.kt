package net.rubygrapefruit.file

internal abstract class AbstractFileSystemElement : FileSystemElement {
    override fun toString(): String {
        return absolutePath
    }

    override val name: String
        get() = path.name

    override val absolutePath: String
        get() = path.absolutePath

    protected fun visitTopDown(dir: Directory, visitor: (DirectoryEntry) -> Unit) {
        val result = dir.listEntries()
        if (result !is Success) {
            throw FileSystemException("Could not visit entries of $dir.")
        }
        val dirEntry = object : DirectoryEntry {
            override val path: ElementPath
                get() = dir.path

            override val type: ElementType
                get() = ElementType.Directory

            override fun snapshot(): Result<ElementSnapshot> {
                return dir.snapshot()
            }

            override fun toDir(): Directory {
                return dir
            }

            override fun toFile(): RegularFile {
                return dir.toFile()
            }

            override fun toSymLink(): SymLink {
                return dir.toSymLink()
            }
        }

        val queue = mutableListOf<DirectoryEntry>(dirEntry)
        while (queue.isNotEmpty()) {
            val entry = queue.removeFirst()
            visitor(entry)
            if (entry.type == ElementType.Directory) {
                val childDir = entry.toDir()
                val childResult = childDir.listEntries()
                if (childResult !is Success) {
                    throw FileSystemException("Could not visit entries of $childDir.")
                }
                queue.addAll(0, childResult.get())
            }
        }
    }
}