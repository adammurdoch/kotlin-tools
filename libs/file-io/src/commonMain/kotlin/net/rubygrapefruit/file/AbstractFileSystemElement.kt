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

        visitor(DirBackedEntry(dir))

        val queue = result.toMutableList()
        while (queue.isNotEmpty()) {
            val entry = queue.removeFirst()
            visitor(entry)
            if (entry.type == ElementType.Directory) {
                val childDir = entry.toDir()
                val childResult = childDir.listEntries()
                queue.addAll(childResult)
            }
        }
    }

    private fun visitBottomUp(dir: Directory, entries: List<DirectoryEntry>, visitor: (DirectoryEntry) -> Unit) {
        val visiting = mutableSetOf<DirectoryEntry>()
        val queue = entries.toMutableList()
        while (queue.isNotEmpty()) {
            val entry = queue.first()
            if (entry.type == ElementType.Directory && visiting.add(entry)) {
                val childDir = entry.toDir()
                val childResult = childDir.listEntries()
                queue.addAll(0, childResult)
            } else {
                queue.removeFirst()
                visitor(entry)
            }
        }

        visitor(DirBackedEntry(dir))
    }

    protected fun deleteRecursively(dir: Directory, delete: (DirectoryEntry) -> Unit) {
        val result = try {
            dir.listEntries()
        } catch (_: MissingDirectoryException) {
            // Ignore
            return
        } catch (_: NotADirectoryException) {
            throw NotADirectoryException("Could not delete directory ${absolutePath} as it is not a directory.")
        }
        visitBottomUp(dir, result, delete)
    }

    protected fun <T : RegularFile> delete(file: T, delete: (T) -> Unit) {
        val result = metadata()
        when {
            result.missing -> return
            result.regularFile -> delete(file)
            result is Success -> throw deleteFileThatIsNotAFile(absolutePath)
            result is Failed -> throw deleteFile(absolutePath, cause = result.failure)
        }
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