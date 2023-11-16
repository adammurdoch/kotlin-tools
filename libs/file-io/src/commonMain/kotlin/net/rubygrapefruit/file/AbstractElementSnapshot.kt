package net.rubygrapefruit.file

internal abstract class AbstractElementSnapshot : ElementSnapshot {
    override fun snapshot(): Result<ElementSnapshot> {
        return Success(this)
    }

    final override fun toFile(): RegularFile {
        if (metadata !is RegularFileMetadata) {
            throw IllegalStateException("$absolutePath is not a regular file.")
        }
        return asRegularFile()
    }

    final override fun toDir(): Directory {
        if (metadata !is DirectoryMetadata) {
            throw IllegalStateException("$absolutePath is not a directory.")
        }
        return asDirectory()
    }

    final override fun toSymLink(): SymLink {
        if (metadata !is SymlinkMetadata) {
            throw IllegalStateException("$absolutePath is not a symlink.")
        }
        return asSymLink()
    }

    abstract fun asDirectory(): Directory

    abstract fun asRegularFile(): RegularFile

    abstract fun asSymLink(): SymLink
}