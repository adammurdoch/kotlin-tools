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
        container.add(JvmCliApp(name))
    }

    fun deriveNative(name: String, config: DerivedNativeCliAppBuilder.() -> Unit = {}) {
        val builder = DerivedNativeCliAppBuilder(name, container)
        builder.config()
        builder.register()
    }

    internal fun register(): JvmCliApp {
        return container.add(JvmCliApp(name))
    }
}

class NativeCliAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : CliAppBuilder() {
    fun derive(name: String, config: DerivedNativeCliAppBuilder.() -> Unit) {
        container.add(NativeCliApp(name))
    }

    internal fun register(): NativeCliApp {
        return container.add(NativeCliApp(name))
    }
}