package net.rubygrapefruit.plugins.internal

sealed class UiAppBuilder

class JvmUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : UiAppBuilder() {
    private val derived = mutableListOf<DerivedJvmUiAppBuilder>()
    private val expectedOutput = mutableListOf<String>()

    fun derive(name: String, config: DerivedJvmUiAppBuilder.() -> Unit = {}) {
        val builder = DerivedJvmUiAppBuilder(name, container)
        builder.config()
        derived.add(builder)
    }

    fun expectedOutput(text: String) {
        expectedOutput.add(text)
    }

    internal fun register(): JvmUiApp {
        val app = container.add(name) { name, sampleDir ->
            val expectedOutput = if (expectedOutput.isEmpty()) null else expectedOutput
            JvmUiApp(name, sampleDir, null, expectedOutput, OriginSourceTree(sampleDir, "src/main", "src/test"))
        }
        for (builder in derived) {
            builder.register(app.sourceTree)
        }
        return app
    }
}

class NativeUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : UiAppBuilder() {
    private val derived = mutableListOf<DerivedNativeUiAppBuilder>()

    fun derive(name: String, config: DerivedNativeUiAppBuilder.() -> Unit = {}) {
        val builder = DerivedNativeUiAppBuilder(name, container)
        builder.config()
        derived.add(builder)
    }

    internal fun register(): NativeUiApp {
        val app = container.add(name) { name, sampleDir ->
            NativeUiApp(name, sampleDir, null, OriginSourceTree(sampleDir, "src/macosMain", "src/macosTest"))
        }
        for (builder in derived) {
            builder.register(app.sourceTree)
        }
        return app
    }
}