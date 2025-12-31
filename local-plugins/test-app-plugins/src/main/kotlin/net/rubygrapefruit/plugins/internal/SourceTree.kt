package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed interface SourceTree {
    val origin: OriginSourceDir

    val dirs: List<Path>
}

class OriginSourceDir(val srcDir: Path) : SourceTree {
    override val origin: OriginSourceDir
        get() = this

    override val dirs: List<Path>
        get() = listOf(srcDir)
}

class GeneratedSourceDir(val srcDir: Path, override val origin: OriginSourceDir) : SourceTree {
    override val dirs: List<Path>
        get() = listOf(srcDir)
}

fun  SourceTree?.derive(srcDir: Path): SourceTree {
    return if (this == null) {
        OriginSourceDir(srcDir)
    } else {
        GeneratedSourceDir(srcDir, origin)
    }
}