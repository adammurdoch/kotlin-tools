package net.rubygrapefruit.file

internal abstract class AbstractElementSnapshot : ElementSnapshot {
    override fun snapshot(): Result<ElementSnapshot> {
        return Success(this)
    }

    final override fun toFile(): RegularFile {
        return asRegularFile()
    }

    final override fun toDir(): Directory {
        return asDirectory()
    }

    final override fun toSymLink(): SymLink {
        return asSymLink()
    }

    abstract fun asDirectory(): Directory

    abstract fun asRegularFile(): RegularFile

    abstract fun asSymLink(): SymLink
}