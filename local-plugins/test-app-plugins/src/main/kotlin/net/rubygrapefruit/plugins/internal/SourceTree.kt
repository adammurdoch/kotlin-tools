package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed interface SourceTree {
    val dirs: List<Path>

    fun derive(srcDir: Path): SourceTree

    fun generatedInto(sampleDir: Path): SourceTree
}

object NoSourceDirs : SourceTree {
    override val dirs: List<Path>
        get() = emptyList()

    override fun derive(srcDir: Path): SourceTree {
        throw IllegalStateException()
    }

    override fun generatedInto(sampleDir: Path): SourceTree {
        return this
    }
}

sealed interface SourceDir : SourceTree {
    val srcDir: Path

    override abstract fun generatedInto(sampleDir: Path): SourceDir
}

class OriginSourceDir(val sampleDir: Path, val path: String) : SourceDir {
    override val srcDir: Path
        get() = sampleDir.resolve(path)

    override val dirs: List<Path>
        get() = listOf(srcDir)

    override fun derive(srcDir: Path): SourceTree {
        return GeneratedSourceDir(srcDir, this)
    }

    override fun generatedInto(sampleDir: Path): SourceDir {
        return GeneratedSourceDir(sampleDir, this)
    }
}

class GeneratedSourceDir(override val srcDir: Path, val origin: OriginSourceDir) : SourceDir {
    override val dirs: List<Path>
        get() = listOf(srcDir)

    override fun derive(srcDir: Path): SourceTree {
        return GeneratedSourceDir(srcDir, origin)
    }

    override fun generatedInto(sampleDir: Path): SourceDir {
        return origin.generatedInto(sampleDir)
    }
}

class CandidateSourceDirs(val srcDirs: List<SourceDir>) : SourceTree {
    override val dirs: List<Path>
        get() = srcDirs.flatMap { it.dirs }

    override fun derive(srcDir: Path): SourceTree {
        throw IllegalStateException()
    }

    override fun generatedInto(sampleDir: Path): SourceTree {
        return CandidateSourceDirs(srcDirs.map { it.generatedInto(sampleDir) })
    }
}

fun SourceTree?.generatedInto(sampleDir: Path, path: String): SourceTree {
    return if (this == null) {
        OriginSourceDir(sampleDir, path)
    } else {
        generatedInto(sampleDir)
    }
}