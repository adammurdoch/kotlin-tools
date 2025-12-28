package net.rubygrapefruit.plugins.internal

sealed class LibBuilder

class JvmLibBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : LibBuilder() {
    fun derive(name: String) {
        container.add(JvmLib(name))
    }

    internal fun register(): JvmLib {
        return container.add(JvmLib(name))
    }
}

class KmpLibBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : LibBuilder() {
    fun derive(name: String) {
        container.add(KmpLib(name))
    }

    internal fun register(): KmpLib {
        return container.add(KmpLib(name))
    }
}