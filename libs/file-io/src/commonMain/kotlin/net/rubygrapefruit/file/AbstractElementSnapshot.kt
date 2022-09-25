package net.rubygrapefruit.file

internal abstract class AbstractElementSnapshot : ElementSnapshot {
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

    abstract fun asDirectory(): Directory

    abstract fun asRegularFile(): RegularFile
}