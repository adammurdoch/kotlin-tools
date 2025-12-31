package net.rubygrapefruit.plugins.internal

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
    name: String,
    private val container: SampleContainer
) : CliAppBuilder() {
    private val derived = mutableListOf<DerivedCliAppBuilder>()
    private val builder = DerivedJvmCliAppBuilder(name, container)

    fun derive(name: String, config: DerivedJvmCliAppBuilder.() -> Unit) {
        val builder = DerivedJvmCliAppBuilder(name, container)
        builder.config()
        derived.add(builder)
    }

    fun deriveNative(name: String, config: DerivedNativeCliAppBuilder.() -> Unit = {}) {
        val builder = DerivedNativeCliAppBuilder(name, container)
        builder.config()
        derived.add(builder)
    }

    internal fun register(): JvmCliApp {
        val app = builder.register(cliArgs.toList(), expectedOutput, null)
        for (builder in derived) {
            builder.register(cliArgs.toList(), expectedOutput, app.sourceTree)
        }
        return app
    }
}

class NativeCliAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : CliAppBuilder() {
    private val derived = mutableListOf<DerivedNativeCliAppBuilder>()

    fun derive(name: String, config: DerivedNativeCliAppBuilder.() -> Unit) {
        val builder = DerivedNativeCliAppBuilder(name, container)
        builder.config()
        derived.add(builder)
    }

    internal fun register(): NativeCliApp {
        val app = container.add(name) { name, sampleDir ->
            val sourceDir = OriginSourceDir(sampleDir.resolve("src/commonMain"))
            NativeCliApp(name, sampleDir, null, cliArgs.toList(), expectedOutput, sourceDir)
        }
        for (builder in derived) {
            builder.register(cliArgs.toList(), expectedOutput, app.sourceTree)
        }
        return app
    }
}