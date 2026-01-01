package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed class LibBuilder {
    protected var sourceDirs = true

    fun noSourceDirs() {
        sourceDirs = false
    }

    protected fun sourceTree(sampleDir: Path, vararg paths: String): SourceTree {
        return if (sourceDirs) {
            if (paths.size == 1) {
                OriginSourceDir(sampleDir, paths.first())
            } else {
                CandidateSourceDirs(paths.map { OriginSourceDir(sampleDir, it) })
            }
        } else {
            NoSourceDirs
        }
    }
}

class JvmLibBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : LibBuilder() {
    private val derived = mutableListOf<String>()

    fun derive(name: String) {
        derived.add(name)
    }

    internal fun register(): JvmLib {
        val lib = container.add(name) { name, sampleDir ->
            val sourceTree = sourceTree(sampleDir, "src/main")
            JvmLib(name, sourceTree)
        }
        for (name in derived) {
            container.add(name) { name, sampleDir ->
                JvmLib(name, lib.sourceTree.generatedInto(sampleDir))
            }
        }
        return lib
    }
}

class KmpLibBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : LibBuilder() {
    private val derived = mutableListOf<String>()

    fun derive(name: String) {
        derived.add(name)
    }

    internal fun register(): KmpLib {
        val lib = container.add(name) { name, sampleDir ->
            val sourceTree = sourceTree(sampleDir, "src/commonMain", "src/desktopMain", "src/jvmMain", "src/mingwMain", "src/unixMain")
            KmpLib(name, sourceTree)
        }
        for (name in derived) {
            container.add(name) { name, sampleDir ->
                KmpLib(name, lib.sourceTree.generatedInto(sampleDir))
            }
        }
        return lib
    }
}