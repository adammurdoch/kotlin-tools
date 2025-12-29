package net.rubygrapefruit.plugins.internal

sealed class LibBuilder

class JvmLibBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : LibBuilder() {
    fun derive(name: String) {
        container.add(name, ::JvmLib)
    }

    internal fun register(): JvmLib {
        return container.add(name, ::JvmLib)
    }
}

class KmpLibBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : LibBuilder() {
    fun derive(name: String) {
        container.add(name, ::KmpLib)
    }

    internal fun register(): KmpLib {
        return container.add(name, ::KmpLib)
    }
}