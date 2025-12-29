package net.rubygrapefruit.plugins.internal

sealed class CliAppBuilder {
    fun cliArgs(vararg args: String) {
    }

    fun expectedOutput(text: String) {
    }
}

class JvmCliAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : CliAppBuilder() {
    fun derive(name: String, config: DerivedJvmCliAppBuilder.() -> Unit) {
        container.add(name, ::JvmCliApp)
    }

    fun deriveNative(name: String, config: DerivedNativeCliAppBuilder.() -> Unit = {}) {
        val builder = DerivedNativeCliAppBuilder(name, container)
        builder.config()
        builder.register()
    }

    internal fun register(): JvmCliApp {
        return container.add(name, ::JvmCliApp)
    }
}

class NativeCliAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : CliAppBuilder() {
    fun derive(name: String, config: DerivedNativeCliAppBuilder.() -> Unit) {
        container.add(name, ::NativeCliApp)
    }

    internal fun register(): NativeCliApp {
        return container.add(name, ::NativeCliApp)
    }
}