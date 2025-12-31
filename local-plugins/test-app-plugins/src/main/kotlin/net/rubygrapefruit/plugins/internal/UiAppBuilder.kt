package net.rubygrapefruit.plugins.internal

sealed class UiAppBuilder

class JvmUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : UiAppBuilder() {
    private val derived = mutableListOf<DerivedJvmUiAppBuilder>()

    fun derive(name: String, config: DerivedJvmUiAppBuilder.() -> Unit = {}) {
        val builder = DerivedJvmUiAppBuilder(name, container)
        builder.config()
        derived.add(builder)
    }

    internal fun register(): JvmUiApp {
        val app = container.add(name) { name, sampleDir ->
            JvmUiApp(name, sampleDir, null, OriginSourceDir(sampleDir.resolve("src/main")))
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
            NativeUiApp(name, sampleDir, null, OriginSourceDir(sampleDir.resolve("src/macosMain")))
        }
        for (builder in derived) {
            builder.register(app.sourceTree)
        }
        return app
    }
}