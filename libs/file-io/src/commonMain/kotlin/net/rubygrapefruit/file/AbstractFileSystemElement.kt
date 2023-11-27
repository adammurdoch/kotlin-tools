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
        if (result is Failed) {
            throw FileSystemException("Could not visit entries of $dir.", result.failure)
        }

        visitor(DirBackedEntry(dir))

        val queue = result.get().toMutableList()
        while (queue.isNotEmpty()) {
            val entry = queue.removeFirst()
            visitor(entry)
            if (entry.type == ElementType.Directory) {
                val childDir = entry.toDir()
                val childResult = childDir.listEntries()
                if (childResult is Failed) {
                    throw FileSystemException("Could not visit entries of $childDir.", childResult.failure)
                }
                queue.addAll(childResult.get())
            }
        }
    }

    protected fun visitBottomUp(dir: Directory, visitor: (DirectoryEntry) -> Unit) {
        val result = dir.listEntries()
        if (result is Failed) {
            throw FileSystemException("Could not visit entries of $dir.", result.failure)
        }

        val visiting = mutableSetOf<DirectoryEntry>()
        val queue = result.get().toMutableList()
        while (queue.isNotEmpty()) {
            val entry = queue.first()
            if (entry.type == ElementType.Directory && visiting.add(entry)) {
                val childDir = entry.toDir()
                val childResult = childDir.listEntries()
                if (childResult is Failed) {
                    throw FileSystemException("Could not visit entries of $childDir.", childResult.failure)
                }
                queue.addAll(0, childResult.get())
            } else {
                queue.removeFirst()
                visitor(entry)
            }
        }

        visitor(DirBackedEntry(dir))
    }

    protected fun deleteRecursively(dir: Directory, delete: (DirectoryEntry) -> Unit) {
        visitBottomUp(dir, delete)
    }

    private class DirBackedEntry(val dir: Directory) : DirectoryEntry {
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
}