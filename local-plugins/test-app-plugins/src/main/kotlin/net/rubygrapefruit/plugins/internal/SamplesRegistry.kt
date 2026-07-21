package net.rubygrapefruit.plugins.internal

import net.rubygrapefruit.bytecode.BytecodeReader
import net.rubygrapefruit.bytecode.ClassFileVisitor
import net.rubygrapefruit.machine.info.Architecture
import net.rubygrapefruit.machine.info.Architecture.Arm64
import net.rubygrapefruit.machine.info.Architecture.X64
import net.rubygrapefruit.machine.info.Machine
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.initialization.Settings
import org.gradle.internal.extensions.core.serviceOf
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Path
import java.security.MessageDigest
import java.util.zip.ZipInputStream
import kotlin.io.path.*

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

        rootProject.tasks.register("generateSamples") { task ->
            val samples = samples.toList()
            task.doLast {
                generateSamples(samples)
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

private fun Lib.verifyLib(project: Project): SampleTasks {
    project.tasks.register("verifySample") { task ->
        task.dependsOn("build")
        task.doLast {
            println("Lib: $name")
            verifySourceTree()
            verifyJvmTarget(jvm, this)
        }
    }
    return SampleTasks(":$name:verifySample", emptyList())
}

private fun verifyJvmTarget(jvmTarget: JvmTarget?, sample: Sample) {
    if (jvmTarget == null) {
        return
    }
    val libs = sample.sourceTree.sampleDir.resolve(jvmTarget.libDirPath)
    val jar = libs.listDirectoryEntries("${jvmTarget.jarNamePrefix}*.jar").singleOrNull()
    if (jar == null) {
        throw IllegalStateException("Could not find library Jar in $libs")
    }
    println("Jar: $jar")
    var moduleInfoSeen = false
    var classSeen = false
    val visitor = object : ClassFileVisitor {
        override fun version(javaVersion: Int) {
            if (javaVersion != jvmTarget.jvmVersion) {
                throw IllegalStateException("Unexpected Java version $javaVersion found in jar: $jar")
            }
            classSeen = true
        }
    }
    jar.inputStream().use { stream ->
        val zip = ZipInputStream(stream)
        do {
            val entry = zip.nextEntry ?: break
            if (entry.name == "module-info.class") {
                moduleInfoSeen = true
            } else if (entry.name.endsWith(".class")) {
                BytecodeReader().readFrom(zip, visitor)
            }
        } while (true)
    }
    if (!moduleInfoSeen) {
        throw IllegalStateException("No module-info entry found in $jar")
    }
    if (!classSeen) {
        throw IllegalStateException("No JVM classes found in $jar")
    }
}

private fun App.verifyApp(project: Project): SampleTasks {
    val toolchainService = project.serviceOf<JavaToolchainService>()
    val execOperations = project.serviceOf<ExecOperations>()
    project.tasks.register("verifySample") { task ->
        task.dependsOn("check")
        applyVerificationToTask(distribution, task, toolchainService, execOperations)
    }
    if (otherDistributions.isNotEmpty()) {
        project.tasks.register("verifyOtherDistributions") { task ->
            for (distribution in otherDistributions) {
                applyVerificationToTask(distribution, task, toolchainService, execOperations)
            }
        }
        return SampleTasks(":$name:verifySample", listOf(":$name:verifyOtherDistributions"))
    } else {
        return SampleTasks(":$name:verifySample", emptyList())
    }
}

private fun App.applyVerificationToTask(distribution: AppDistribution, task: Task, toolchainService: JavaToolchainService, execOperations: ExecOperations) {
    if (!distribution.canBuild) {
        task.doLast {
            println("App $name not buildable")
        }
        return
    }
    task.dependsOn(distribution.distTask)
    task.doLast {
        verify(this, distribution, toolchainService, execOperations)
    }
}

private fun verify(app: App, distribution: AppDistribution, toolchainService: JavaToolchainService, execOperations: ExecOperations) {
    when (app) {
        is CliApp -> println("CLI app: ${app.name} dist: ${distribution.distTask}")
        is UiApp -> println("UI app: ${app.name} dist: ${distribution.distTask}")
    }

    app.verifySourceTree()
    verifyJvmTarget(app.jvm, app)

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
            if (Machine.thisMachine.isMacOS) {
                val architecture = path.architecture(execOperations)
                if (architecture != binaries.architecture) {
                    throw IllegalStateException("Unexpected architecture for binary $path: $architecture")
                }
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
            val javaBinDir = if (distribution.invocation is ScriptInvocationWithSystemJvm) {
                val java = toolchainService.launcherFor {
                    it.languageVersion.set(JavaLanguageVersion.of(distribution.invocation.jvmVersion))
                }.get().executablePath.asFile.parentFile.toPath()
                println("Java bin dir: $java")
                java
            } else {
                null
            }
            val outputStream = ByteArrayOutputStream()
            val result = execOperations.exec {
                it.commandLine(commandLine)
                if (javaBinDir != null) {
                    it.environment("PATH", javaBinDir.absolutePathString() + File.pathSeparatorChar + System.getenv("PATH"))
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
            for (text in expectedOutput) {
                if (!outputText.contains(text)) {
                    throw IllegalStateException("Expected text '$text' not found in output.")
                }
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

private fun Sample.verifySourceTree() {
    val dirs = mutableListOf<SourceDir>()
    sourceTree.visit { dirs.add(it) }
    if (dirs.isEmpty()) {
        println("No source dirs")
        return
    }
    var hasSrcDir = false
    for (dir in dirs) {
        if (dir.srcDir.isDirectory()) {
            println("Source dir: ${dir.srcDir}")
            hasSrcDir = true
        }
        if (dir is GeneratedSourceDir) {
            dir.visitContents({ source, dest ->
                if (!dest.exists()) {
                    val type = if (source.isDirectory()) {
                        "directory"
                    } else {
                        "file"
                    }
                    throw IllegalStateException("Source $type missing, maybe regenerate samples: $dest")
                }
                if (source.isRegularFile()) {
                    val srcHash = source.hash()
                    val destHash = dest.hash()
                    if (!srcHash.contentEquals(destHash)) {
                        throw IllegalStateException("Source file has incorrect content, maybe regenerate samples: $dest")
                    }
                }
            }, { path ->
                throw IllegalStateException("Unexpected file in source dir, maybe regenerate samples: $path")
            })
        }
    }
    if (!hasSrcDir) {
        throw IllegalStateException("Sample ${name} does not contain any source directories.")
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

private fun generateSamples(samples: List<Sample>) {
    for (sample in samples) {
        sample.sourceTree.visit { srcDir ->
            if (srcDir is GeneratedSourceDir && srcDir.origin.srcDir.isDirectory()) {
                srcDir.visitContents({ source, dest ->
                    if (source.isRegularFile()) {
                        source.copyTo(dest, overwrite = true)
                    } else {
                        dest.createDirectories()
                    }
                }, { path ->
                    path.deleteIfExists()
                })
            }
        }
    }
}

private fun GeneratedSourceDir.visitContents(action: (Path, Path) -> Unit, extraAction: (Path) -> Unit) {
    val sourceDir = origin.srcDir
    val destDir = srcDir
    val files = mutableListOf<Path>()
    sourceDir.walk(PathWalkOption.INCLUDE_DIRECTORIES).forEach { source ->
        val dest = srcDir.resolve(source.relativeTo(sourceDir))
        files.add(dest)
        action(source, dest)
    }

    val extra = mutableListOf<Path>()
    destDir.walk(PathWalkOption.INCLUDE_DIRECTORIES).forEach { dest ->
        if (!files.contains(dest) && (dest.isDirectory() || dest.name.endsWith(".kt"))) {
            extra.add(0, dest)
        }
    }
    for (path in extra) {
        extraAction(path)
    }
}

private fun Path.hash(): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(readBytes())
    return digest.digest()
}

private class SampleTasks(val verifyTaskName: String, val otherTaskNames: List<String>)