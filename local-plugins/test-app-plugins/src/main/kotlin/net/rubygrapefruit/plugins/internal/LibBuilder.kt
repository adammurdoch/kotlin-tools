package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed class LibBuilder {
    protected var sourceDirs = true

    fun noSourceDirs() {
        sourceDirs = false
    }

    protected fun sourceTree(sampleDir: Path, path: String): SourceTree {
        return if (sourceDirs) {
            OriginSourceDir(sampleDir.resolve(path))
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
                JvmLib(name, lib.sourceTree.derive(sampleDir.resolve("src/main")))
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
            val sourceTree = sourceTree(sampleDir, "src/commonMain")
            KmpLib(name, sourceTree)
        }
        for (name in derived) {
            container.add(name) { name, sampleDir ->
                KmpLib(name, lib.sourceTree.derive(sampleDir.resolve("src/commonMain")))
            }
        }
        return lib
    }
}