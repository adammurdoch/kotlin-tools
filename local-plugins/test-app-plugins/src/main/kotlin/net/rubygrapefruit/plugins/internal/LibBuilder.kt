package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed class LibBuilder {
    protected var sourceDirs = true

    fun noSourceDirs() {
        sourceDirs = false
    }

    protected fun sourceTree(sampleDir: Path, main: String, test: String, vararg additionalPaths: String): SourceTree {
        return if (sourceDirs) {
            OriginSourceTree(sampleDir, main, test, additionalPaths.toList())
        } else {
            NoSourceDirs(sampleDir)
        }
    }
}

class JvmLibBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : LibBuilder() {
    private val derived = mutableListOf<DerivedJvmLibBuilder>()

    fun derive(name: String, config: DerivedJvmLibBuilder.() -> Unit = {}) {
        val builder = DerivedJvmLibBuilder(name)
        builder.config()
        derived.add(builder)
    }

    internal fun register(): JvmLib {
        val lib = container.add(name) { name, sampleDir ->
            val sourceTree = sourceTree(sampleDir, main = "src/main", test = "src/test")
            JvmLib(name, sourceTree, 17)
        }
        for (builder in derived) {
            container.add(builder.name) { name, sampleDir ->
                JvmLib(name, lib.sourceTree.generatedInto(sampleDir), builder.jvmVersion)
            }
        }
        return lib
    }
}

class KmpLibBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : LibBuilder() {
    private var jvm = true
    private val derived = mutableListOf<DerivedKmpLibBuilder>()

    fun noJvm() {
        jvm = false
    }

    fun derive(name: String, config: DerivedKmpLibBuilder.() -> Unit = {}) {
        val builder = DerivedKmpLibBuilder(name)
        builder.config()
        derived.add(builder)
    }

    internal fun register(): KmpLib {
        val lib = container.add(name) { name, sampleDir ->
            val sourceTree = sourceTree(
                sampleDir,
                main = "src/commonMain",
                test = "src/commonTest",
                "src/desktopMain",
                "src/jvmMain",
                "src/mingwMain",
                "src/unixMain",
                "src/jsMain",
            )
            KmpLib(name, sourceTree, if (jvm) 17 else null)
        }
        for (builder in derived) {
            container.add(builder.name) { name, sampleDir ->
                KmpLib(name, lib.sourceTree.generatedInto(sampleDir), builder.jvmVersion)
            }
        }
        return lib
    }
}