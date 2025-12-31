package net.rubygrapefruit.plugins.internal

sealed class LibBuilder

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
            JvmLib(name, OriginSourceDir(sampleDir.resolve("src/main")))
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
            KmpLib(name, OriginSourceDir(sampleDir.resolve("src/commonMain")))
        }
        for (name in derived) {
            container.add(name) { name, sampleDir ->
                KmpLib(name, lib.sourceTree.derive(sampleDir.resolve("src/commonMain")))
            }
        }
        return lib
    }
}