package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed interface SourceTree {
    val dirs: List<Path>

    fun derive(srcDir: Path): SourceTree
}

object NoSourceDirs : SourceTree {
    override val dirs: List<Path>
        get() = emptyList()

    override fun derive(srcDir: Path): SourceTree {
        throw IllegalStateException()
    }
}

sealed interface SourceDir : SourceTree {
    val srcDir: Path
}

class OriginSourceDir(override val srcDir: Path) : SourceDir {
    override val dirs: List<Path>
        get() = listOf(srcDir)

    override fun derive(srcDir: Path): SourceTree {
        return GeneratedSourceDir(srcDir, this)
    }
}

class GeneratedSourceDir(override val srcDir: Path, val origin: OriginSourceDir) : SourceDir {
    override val dirs: List<Path>
        get() = listOf(srcDir)

    override fun derive(srcDir: Path): SourceTree {
        return GeneratedSourceDir(srcDir, origin)
    }
}

fun SourceTree?.derive(srcDir: Path): SourceTree {
    return if (this == null) {
        OriginSourceDir(srcDir)
    } else {
        derive(srcDir)
    }
}