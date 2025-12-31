package net.rubygrapefruit.plugins.internal

import net.rubygrapefruit.machine.info.Machine

sealed class DerivedCliAppBuilder {
    internal abstract fun register(cliArgs: List<String>, expectedOutput: String?, derivedFrom: SourceTree?): Sample
}

class DerivedJvmCliAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : DerivedCliAppBuilder() {
    private var launcher: String? = null
    private var jvmVersion: Int? = null
    private var embedded = false
    private var native = false

    fun launcher(name: String) {
        launcher = name
    }

    fun requiresJvm(version: Int) {
        jvmVersion = version
    }

    fun embeddedJvm() {
        jvmVersion = null
        embedded = true
        native = false
    }

    fun nativeBinaries() {
        jvmVersion = null
        embedded = false
        native = true
    }

    override fun register(cliArgs: List<String>, expectedOutput: String?, derivedFrom: SourceTree?): JvmCliApp {
        return container.add(name) { name, sampleDir ->
            val distDir = sampleDir.resolve("build/dist")
            val distribution = when {
                embedded -> {
                    val invocation = ScriptInvocation.of(name, distDir, launcher, cliArgs, expectedOutput)
                    val binaries = AppDistribution.Binaries(Machine.thisMachine.architecture, listOf(distDir.resolve("jvm/bin/java")))
                    CliAppDistribution("dist", distDir, binaries, invocation)
                }

                native -> {
                    val invocation = BinaryInvocation.of(name, distDir, launcher, cliArgs, expectedOutput)
                    val binaries = AppDistribution.Binaries(Machine.thisMachine.architecture, listOf(invocation.binary))
                    CliAppDistribution("dist", distDir, binaries, invocation)
                }

                else -> {
                    val invocation = ScriptInvocationWithInstalledJvm.of(name, distDir, launcher, cliArgs, expectedOutput, jvmVersion)
                    CliAppDistribution("dist", distDir, null, invocation)
                }
            }
            val sourceDir = derivedFrom.derive(sampleDir.resolve("src/main"))
            JvmCliApp(name, distribution, sourceDir)
        }
    }
}

class DerivedNativeCliAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : DerivedCliAppBuilder() {
    private val derived = mutableListOf<DerivedCliAppBuilder>()
    private var launcher: String? = null

    fun launcher(name: String) {
        launcher = name
    }

    fun derive(name: String, config: DerivedNativeCliAppBuilder.() -> Unit = {}) {
        val builder = DerivedNativeCliAppBuilder(name, container)
        builder.config()
        derived.add(builder)
    }

    override fun register(cliArgs: List<String>, expectedOutput: String?, derivedFrom: SourceTree?): NativeCliApp {
        val app = container.add(name) { name, sampleDir ->
            val sourceDir = derivedFrom.derive(sampleDir.resolve("src/commonMain"))
            NativeCliApp(name, sampleDir, launcher, cliArgs, expectedOutput, sourceDir)
        }
        for (builder in derived) {
            builder.register(cliArgs, expectedOutput, app.sourceTree)
        }
        return app
    }
}

sealed class DerivedUiAppBuilder {
    protected var launcher: String? = null

    fun launcher(name: String) {
        launcher = name
    }
}

class DerivedJvmUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : DerivedUiAppBuilder() {
    fun register(derivedFrom: SourceTree?): JvmUiApp {
        return container.add(name) { name, sampleDir ->
            val sourceDir = derivedFrom.derive(sampleDir.resolve("src/main"))
            JvmUiApp(name, sampleDir, launcher, sourceDir)
        }
    }
}

class DerivedNativeUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : DerivedUiAppBuilder() {
    fun register(derivedFrom: SourceTree?): NativeUiApp {
        return container.add(name) { name, sampleDir ->
            val sourceDir = derivedFrom.derive(sampleDir.resolve("src/macosMain"))
            NativeUiApp(name, sampleDir, launcher, sourceDir)
        }
    }
}