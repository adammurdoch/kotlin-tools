package net.rubygrapefruit.plugins.internal

sealed class DerivedAppBuilder {
}

class DerivedJvmCliAppBuilder : DerivedAppBuilder() {
    fun launcher(name: String) {
    }

    fun requiresJvm(version: Int) {
    }

    fun embeddedJvm() {
    }

    fun nativeBinaries() {
    }
}

class DerivedNativeCliAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : DerivedAppBuilder() {
    fun launcher(name: String) {
    }

    fun derive(name: String, config: DerivedNativeCliAppBuilder.() -> Unit = {}) {
        container.add(name, ::NativeCliApp)
    }

    internal fun register(): NativeCliApp {
        return container.add(name, ::NativeCliApp)
    }
}