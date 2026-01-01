package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed interface SourceTree {
    fun generatedInto(sampleDir: Path): SourceTree

    fun generatedInto(sampleDir: Path, path: String): SourceTree

    fun visit(visitor: (SourceDir) -> Unit)
}

object NoSourceDirs : SourceTree {
    override fun generatedInto(sampleDir: Path): SourceTree {
        return this
    }

    override fun generatedInto(sampleDir: Path, path: String): SourceTree {
        return this
    }

    override fun visit(visitor: (SourceDir) -> Unit) {
    }
}

sealed class SourceDir : SourceTree {
    abstract val srcDir: Path

    abstract override fun generatedInto(sampleDir: Path): SourceDir

    override fun visit(visitor: (SourceDir) -> Unit) {
        visitor(this)
    }
}

class OriginSourceDir(val sampleDir: Path, val path: String) : SourceDir() {
    override val srcDir: Path
        get() = sampleDir.resolve(path)

    override fun generatedInto(sampleDir: Path): SourceDir {
        return generatedInto(sampleDir, path)
    }

    override fun generatedInto(sampleDir: Path, path: String): SourceDir {
        return GeneratedSourceDir(sampleDir.resolve(path), this)
    }
}

class GeneratedSourceDir(override val srcDir: Path, val origin: OriginSourceDir) : SourceDir() {
    override fun generatedInto(sampleDir: Path): SourceDir {
        return origin.generatedInto(sampleDir)
    }

    override fun generatedInto(sampleDir: Path, path: String): SourceTree {
        return origin.generatedInto(sampleDir, path)
    }
}

class CandidateSourceDirs(val srcDirs: List<SourceDir>) : SourceTree {
    override fun generatedInto(sampleDir: Path): SourceTree {
        return CandidateSourceDirs(srcDirs.map { it.generatedInto(sampleDir) })
    }

    override fun generatedInto(sampleDir: Path, path: String): SourceTree {
        throw IllegalStateException()
    }

    override fun visit(visitor: (SourceDir) -> Unit) {
        for (sourceDir in srcDirs) {
            sourceDir.visit(visitor)
        }
    }
}

fun SourceTree?.generatedInto(sampleDir: Path, path: String): SourceTree {
    return if (this == null) {
        OriginSourceDir(sampleDir, path)
    } else {
        generatedInto(sampleDir, path)
    }
}