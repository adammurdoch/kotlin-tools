package net.rubygrapefruit.plugins.internal

import net.rubygrapefruit.machine.info.Architecture
import net.rubygrapefruit.machine.info.Architecture.Arm64
import net.rubygrapefruit.machine.info.Architecture.X64
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.internal.extensions.core.serviceOf
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

abstract class SamplesRegistry(private val settings: Settings) : SampleContainer {
    private val samples = mutableListOf<Sample>()

    fun jvmLib(name: String, config: JvmLibBuilder.() -> Unit = {}): JvmLib {
        val builder = JvmLibBuilder(name, this)
        builder.config()
        return builder.register()
    }

    fun kmpLib(name: String, config: KmpLibBuilder.() -> Unit = {}): KmpLib {
        val builder = KmpLibBuilder(name, this)
        builder.config()
        return builder.register()
    }

    fun jvmCliApp(name: String, config: JvmCliAppBuilder.() -> Unit = {}): JvmCliApp {
        val builder = JvmCliAppBuilder(name, this)
        builder.config()
        return builder.register()
    }

    fun nativeCliApp(name: String, config: NativeCliAppBuilder.() -> Unit = {}): NativeCliApp {
        val builder = NativeCliAppBuilder(name, this)
        builder.config()
        return builder.register()
    }

    fun jvmUiApp(name: String, config: JvmUiAppBuilder.() -> Unit = {}): JvmUiApp {
        val builder = JvmUiAppBuilder(name, this)
        builder.config()
        return builder.register()
    }

    fun nativeUiApp(name: String, config: NativeUiAppBuilder.() -> Unit = {}): NativeUiApp {
        val builder = NativeUiAppBuilder(name, this)
        builder.config()
        return builder.register()
    }

    override fun <T : Sample> add(name: String, factory: (String, Path) -> T): T {
        val sampleDir = settings.rootDir.resolve(name).toPath()
        val sample = factory(name, sampleDir)
        samples.add(sample)
        settings.include(sample.name)
        return sample
    }

    internal fun applyTo(rootProject: Project) {
        for (f in settings.rootDir.listFiles()) {
            val ignore = listOf(".gradle", ".kotlin", "gradle", "kotlin-js-store", "build")
            if (f.isDirectory && !ignore.contains(f.name)) {
                if (samples.find { it.name == f.name } == null) {
                    throw IllegalStateException("Sample in $f is not declared")
                }
            }
        }

        val sampleTasks = samples.map { sample -> sample.verify(rootProject) }

        val verifySample = rootProject.tasks.register("verifySample") { task ->
            task.dependsOn(sampleTasks.map { it.verifyTaskName })
        }
        val verifyOther = rootProject.tasks.register("verifyOtherDistributions") { task ->
            task.dependsOn(sampleTasks.flatMap { it.otherTaskNames })
        }
        rootProject.tasks.register("smokeTest") {
            it.dependsOn(verifySample, verifyOther)
        }
        rootProject.tasks.register("minTest") {
            it.dependsOn(verifySample)
        }
    }
}

private fun Sample.verify(rootProject: Project): SampleTasks {
    val project = rootProject.project(":${name}")
    return when (this) {
        is Lib -> verifyLib(project)
        is App -> verifyApp(project)
    }
}

private fun App.verifyApp(project: Project): SampleTasks {
    val toolchainService = project.serviceOf<JavaToolchainService>()
    val execOperations = project.serviceOf<ExecOperations>()
    project.tasks.register("verifySample") { task ->
        task.dependsOn(distribution.distTask)
        task.doLast {
            verify(this, distribution, toolchainService, execOperations)
        }
    }
    if (otherDistributions.isNotEmpty()) {
        project.tasks.register("verifyOtherDistributions") { task ->
            for (distribution in otherDistributions) {
                task.dependsOn(distribution.distTask)
                task.doLast {
                    verify(this, distribution, toolchainService, execOperations)
                }
            }
        }
        return SampleTasks(":$name:verifySample", listOf(":$name:verifyOtherDistributions"))
    } else {
        return SampleTasks(":$name:verifySample", emptyList())
    }
}

private fun verify(app: App, distribution: AppDistribution, toolchainService: JavaToolchainService, execOperations: ExecOperations) {
    when (app) {
        is CliApp -> println("CLI app: ${app.name} dist: ${distribution.distTask}")
        is UiApp -> println("UI app: ${app.name} dist: ${distribution.distTask}")
    }

    println("Dist dir: ${distribution.distDir}")
    if (!distribution.distDir.isDirectory()) {
        throw IllegalStateException("Distribution directory ${distribution.distDir} does not exist")
    }

    val binaries = distribution.binaries
    if (binaries != null) {
        for (path in binaries.binaries) {
            println("Binary: $path (${binaries.architecture})")
            if (!path.isRegularFile()) {
                throw IllegalStateException("Binary $path does not exist")
            }
            val architecture = path.architecture(execOperations)
            if (architecture != binaries.architecture) {
                throw IllegalStateException("Unexpected architecture for binary $path: $architecture")
            }
        }
    }

    when (distribution) {
        is CliAppDistribution -> {
            if (!distribution.invocation.launcher.isRegularFile()) {
                throw IllegalStateException("Launcher file ${distribution.invocation.launcher} does not exist")
            }
            val commandLine = distribution.invocation.commandLine
            println("Run: ${commandLine.joinToString(" ")}")
                val javaHome = if (distribution.invocation is ScriptInvocationWithInstalledJvm) {
                val java = toolchainService.launcherFor {
                    it.languageVersion.set(JavaLanguageVersion.of(distribution.invocation.jvmVersion))
                }.get().metadata.installationPath.asFile
                println("Java home: $java")
                java
            } else {
                null
            }
            val outputStream = ByteArrayOutputStream()
            val result = execOperations.exec {
                it.commandLine(commandLine)
                if (javaHome != null) {
                    it.environment("JAVA_HOME", javaHome.absolutePath)
                }
                it.standardOutput = outputStream
                it.errorOutput = outputStream
                it.isIgnoreExitValue = true
            }
            val outputText = outputStream.toString(Charsets.UTF_8)
            println("----")
            println(outputText)
            println("----")
            result.assertNormalExitValue()

            val expectedOutput = distribution.invocation.expectedOutput
            if (expectedOutput != null && !outputText.contains(expectedOutput)) {
                throw IllegalStateException("Expected text '$expectedOutput' not found in output.")
            }
        }

        is UiAppDistribution -> {
            println("Launcher: ${distribution.launcher}")
            if (!distribution.launcher.isRegularFile()) {
                throw IllegalStateException("Launcher file ${distribution.launcher} does not exist")
            }
        }
    }
}

private fun Path.architecture(execOperations: ExecOperations): Architecture {
    val str = ByteArrayOutputStream()
    execOperations.exec {
        it.commandLine("otool", "-hv", absolutePathString())
        it.standardOutput = str
    }
    val arch = str.toString().lines()[3].split(Regex("\\s+"))[1]
    return when (arch) {
        "X86_64" -> X64
        "ARM64" -> Arm64
        else -> throw IllegalArgumentException("Unexpected architecture: $arch")
    }
}

private fun Lib.verifyLib(project: Project): SampleTasks {
    project.tasks.register("verifySample") { task ->
        task.dependsOn("build")
        task.doLast {
            println("Lib: $name")
        }
    }
    return SampleTasks(":$name:verifySample", emptyList())
}

private class SampleTasks(val verifyTaskName: String, val otherTaskNames: List<String>)