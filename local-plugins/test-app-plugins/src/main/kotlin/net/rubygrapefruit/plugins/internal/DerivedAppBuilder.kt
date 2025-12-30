package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed class DerivedAppBuilder {
    internal abstract fun register(): Sample
}

class DerivedJvmCliAppBuilder internal constructor(
    private val name: String,
    private val owner: CliAppBuilder,
    private val container: SampleContainer
) : DerivedAppBuilder() {
    private var launcher: String? = null
    private var jvmVersion: Int? = null

    fun launcher(name: String) {
        launcher = name
    }

    fun requiresJvm(version: Int) {
        jvmVersion = version
    }

    fun embeddedJvm() {
        jvmVersion = null
    }

    fun nativeBinaries() {
        jvmVersion = null
    }

    override fun register(): JvmCliApp {
        return container.add(name) { name, sampleDir ->
            JvmCliApp(name, sampleDir, launcher, owner.cliArgs.toList(), jvmVersion, owner.expectedOutput)
        }
    }
}

class DerivedNativeCliAppBuilder internal constructor(
    private val name: String,
    private val owner: CliAppBuilder,
    private val container: SampleContainer
) : DerivedAppBuilder() {
    private val derived = mutableListOf<DerivedAppBuilder>()
    private var launcher: String? = null

    fun launcher(name: String) {
        launcher = name
    }

    fun derive(name: String, config: DerivedNativeCliAppBuilder.() -> Unit = {}) {
        val builder = DerivedNativeCliAppBuilder(name, owner, container)
        builder.config()
        derived.add(builder)
    }

    override fun register(): NativeCliApp {
        val app = container.add(name, ::create)
        for (builder in derived) {
            builder.register()
        }
        return app
    }

    private fun create(name: String, sampleDir: Path): NativeCliApp {
        return NativeCliApp(name, sampleDir, launcher, owner.cliArgs.toList(), owner.expectedOutput)
    }
}

sealed class DerivedUiAppBuilder : DerivedAppBuilder() {
    protected var launcher: String? = null

    fun launcher(name: String) {
        launcher = name
    }
}

class DerivedJvmUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : DerivedUiAppBuilder() {
    override fun register(): JvmUiApp {
        return container.add(name) { name, sampleDir ->
            JvmUiApp(name, sampleDir, launcher)
        }
    }
}

class DerivedNativeUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : DerivedUiAppBuilder() {
    override fun register(): NativeUiApp {
        return container.add(name) { name, sampleDir ->
            NativeUiApp(name, sampleDir, launcher)
        }
    }
}