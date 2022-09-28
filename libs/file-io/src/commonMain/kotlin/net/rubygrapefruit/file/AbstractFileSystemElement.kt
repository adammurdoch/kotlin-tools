package net.rubygrapefruit.file

internal abstract class AbstractFileSystemElement : FileSystemElement {
    override fun toString(): String {
        return absolutePath
    }

    protected fun visitTopDown(dir: Directory, visitor: (DirectoryEntry) -> Unit) {
        val result = dir.listEntries()
        if (result !is Success) {
            throw FileSystemException("Could not visit entries of $dir.")
        }
        val dirEntry = object : DirectoryEntry {
            override val name: String
                get() = dir.name

            override val type: ElementType
                get() = ElementType.Directory

            override fun toDir(): Directory {
                return dir
            }

            override fun toElement(): FileSystemElement {
                return dir
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