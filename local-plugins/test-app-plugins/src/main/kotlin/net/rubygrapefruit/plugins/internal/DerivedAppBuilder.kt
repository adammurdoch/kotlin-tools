package net.rubygrapefruit.plugins.internal

import net.rubygrapefruit.machine.info.Machine

sealed class DerivedCliAppBuilder {
    internal val expectedOutput: List<String>
        field = mutableListOf()

    fun expectedOutput(text: String) {
        expectedOutput.add(text)
    }

    internal abstract fun register(cliArgs: List<String>, expectedOutput: List<String>, derivedFrom: SourceTree?): Sample
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

    override fun register(cliArgs: List<String>, expectedOutput: List<String>, derivedFrom: SourceTree?): JvmCliApp {
        val mergedExpectedOutput = expectedOutput + this.expectedOutput
        return container.add(name) { name, sampleDir ->
            val distDir = sampleDir.resolve("build/dist")
            val distribution = when {
                embedded -> {
                    val invocation = ScriptInvocation.of(name, distDir, launcher, cliArgs, mergedExpectedOutput)
                    val javaLauncher = distDir.resolve(Machine.thisMachine.executableName("jvm/bin/java"))
                    val binaries = AppDistribution.Binaries(Machine.thisMachine.architecture, listOf(javaLauncher))
                    CliAppDistribution("dist", distDir, binaries, invocation)
                }

                native -> {
                    val invocation = BinaryInvocation.of(name, distDir, launcher, cliArgs, mergedExpectedOutput)
                    val binaries = AppDistribution.Binaries(Machine.thisMachine.architecture, listOf(invocation.binary))
                    CliAppDistribution("dist", distDir, binaries, invocation)
                }

                else -> {
                    val invocation = ScriptInvocationWithSystemJvm.of(name, distDir, launcher, cliArgs, mergedExpectedOutput, jvmVersion)
                    CliAppDistribution("dist", distDir, null, invocation)
                }
            }
            val sourceDir = derivedFrom.generatedInto(sampleDir, "src/main", "src/test")
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

    override fun register(cliArgs: List<String>, expectedOutput: List<String>, derivedFrom: SourceTree?): NativeCliApp {
        val app = container.add(name) { name, sampleDir ->
            val sourceDir = derivedFrom.generatedInto(sampleDir, "src/commonMain", "src/commonTest")
            NativeCliApp(name, sampleDir, launcher, cliArgs, expectedOutput, sourceDir)
        }
        for (builder in derived) {
            builder.register(cliArgs, expectedOutput + this.expectedOutput, app.sourceTree)
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
            val sourceDir = derivedFrom.generatedInto(sampleDir, "src/main", "src/test")
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
            val sourceDir = derivedFrom.generatedInto(sampleDir, "src/macosMain", "src/macosTest")
            NativeUiApp(name, sampleDir, launcher, sourceDir)
        }
    }
}