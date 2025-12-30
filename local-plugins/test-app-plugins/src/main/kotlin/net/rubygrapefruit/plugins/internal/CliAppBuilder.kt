package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed class CliAppBuilder {
    internal val cliArgs = mutableListOf<String>()
    internal var expectedOutput: String? = null
        private set

    fun cliArgs(vararg args: String) {
        cliArgs.clear()
        cliArgs.addAll(args)
    }

    fun expectedOutput(text: String) {
        expectedOutput = text
    }
}

class JvmCliAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : CliAppBuilder() {
    private val derived = mutableListOf<DerivedAppBuilder>()

    fun derive(name: String, config: DerivedJvmCliAppBuilder.() -> Unit) {
        val builder = DerivedJvmCliAppBuilder(name, this, container)
        builder.config()
        derived.add(builder)
    }

    fun deriveNative(name: String, config: DerivedNativeCliAppBuilder.() -> Unit = {}) {
        val builder = DerivedNativeCliAppBuilder(name, this, container)
        builder.config()
        derived.add(builder)
    }

    internal fun register(): JvmCliApp {
        val app = container.add(name, ::create)
        for (builder in derived) {
            builder.register()
        }
        return app
    }

    private fun create(name: String, sampleDir: Path): JvmCliApp {
        return JvmCliApp(name, sampleDir, null, cliArgs.toList(), null, expectedOutput)
    }
}

class NativeCliAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : CliAppBuilder() {
    private val derived = mutableListOf<DerivedAppBuilder>()

    fun derive(name: String, config: DerivedNativeCliAppBuilder.() -> Unit) {
        val builder = DerivedNativeCliAppBuilder(name, this, container)
        builder.config()
        derived.add(builder)
    }

    internal fun register(): NativeCliApp {
        val app = container.add(name, ::create)
        for (builder in derived) {
            builder.register()
        }
        return app
    }

    private fun create(name: String, sampleDir: Path): NativeCliApp {
        return NativeCliApp(name, sampleDir, null, cliArgs.toList(), expectedOutput)
    }
}