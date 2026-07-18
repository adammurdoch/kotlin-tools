package net.rubygrapefruit.plugins.internal

sealed class LibBuilder {
    protected var sourceDirs = true

    fun noSourceDirs() {
        sourceDirs = false
    }
}

class JvmLibBuilder internal constructor(
    name: String,
    private val container: SampleContainer
) : LibBuilder() {
    private val derived = mutableListOf<DerivedJvmLibBuilder>()
    private val builder = DerivedJvmLibBuilder(name, container)

    fun derive(name: String, config: DerivedJvmLibBuilder.() -> Unit = {}) {
        val builder = DerivedJvmLibBuilder(name, container)
        builder.config()
        derived.add(builder)
    }

    internal fun register(): JvmLib {
        val lib = builder.register(null)
        for (builder in derived) {
            builder.register(lib.sourceTree)
        }
        return lib
    }
}

class KmpLibBuilder internal constructor(
    name: String,
    private val container: SampleContainer
) : LibBuilder() {
    private val derived = mutableListOf<DerivedKmpLibBuilder>()
    private val builder = DerivedKmpLibBuilder(name, container)

    fun noJvm() {
        builder.noJvm()
    }

    fun derive(name: String, config: DerivedKmpLibBuilder.() -> Unit = {}) {
        val builder = DerivedKmpLibBuilder(name, container)
        builder.config()
        derived.add(builder)
    }

    internal fun register(): KmpLib {
        val lib = builder.register(null)
        for (builder in derived) {
            builder.register(lib.sourceTree)
        }
        return lib
    }
}