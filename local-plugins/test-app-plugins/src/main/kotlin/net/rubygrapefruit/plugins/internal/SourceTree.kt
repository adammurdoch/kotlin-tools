package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed interface SourceTree {
    val sampleDir: Path

    fun generatedInto(sampleDir: Path): SourceTree

    fun generatedInto(sampleDir: Path, main: String, test: String): SourceTree

    fun visit(visitor: (SourceDir) -> Unit)
}

class NoSourceDirs(override val sampleDir: Path) : SourceTree {
    override fun generatedInto(sampleDir: Path): SourceTree {
        return NoSourceDirs(sampleDir)
    }

    override fun generatedInto(sampleDir: Path, main: String, test: String): SourceTree {
        return NoSourceDirs(sampleDir)
    }

    override fun visit(visitor: (SourceDir) -> Unit) {
    }
}

sealed interface SourceDir {
    val srcDir: Path
}

data class OriginSourceDir(val dir: Path, val path: String) : SourceDir {
    override val srcDir: Path get() = dir.resolve(path)
}

data class GeneratedSourceDir(val dir: Path, val path: String, val origin: OriginSourceDir) : SourceDir {
    override val srcDir: Path get() = dir.resolve(path)
}

class OriginSourceTree(override val sampleDir: Path, mainPath: String, testPath: String, additionalPaths: List<String> = emptyList()) : SourceTree {
    val main = OriginSourceDir(sampleDir, mainPath)
    val test = OriginSourceDir(sampleDir, testPath)
    val additional = additionalPaths.map { OriginSourceDir(sampleDir, it) }

    override fun visit(visitor: (SourceDir) -> Unit) {
        visitor(main)
        visitor(test)
        for (dir in additional) {
            visitor(dir)
        }
    }

    override fun generatedInto(sampleDir: Path): SourceTree {
        return generatedInto(sampleDir, main.path, test.path)
    }

    override fun generatedInto(sampleDir: Path, main: String, test: String): SourceTree {
        return GeneratedSourceTree(sampleDir, main, test, this)
    }
}

class GeneratedSourceTree(override val sampleDir: Path, val main: String, val test: String, val origin: OriginSourceTree) : SourceTree {
    override fun visit(visitor: (SourceDir) -> Unit) {
        visitor(GeneratedSourceDir(sampleDir, main, origin.main))
        visitor(GeneratedSourceDir(sampleDir, test, origin.test))
        for (dir in origin.additional) {
            visitor(GeneratedSourceDir(sampleDir, dir.path, dir))
        }
    }

    override fun generatedInto(sampleDir: Path): SourceTree {
        return origin.generatedInto(sampleDir)
    }

    override fun generatedInto(sampleDir: Path, main: String, test: String): SourceTree {
        return origin.generatedInto(sampleDir, main, test)
    }
}

fun SourceTree?.generatedInto(sampleDir: Path, main: String, test: String): SourceTree {
    return if (this == null) {
        OriginSourceTree(sampleDir, main, test, emptyList())
    } else {
        generatedInto(sampleDir, main, test)
    }
}