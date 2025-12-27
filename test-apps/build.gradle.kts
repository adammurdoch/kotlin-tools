import net.rubygrapefruit.machine.info.Architecture
import net.rubygrapefruit.machine.info.Architecture.Arm64
import net.rubygrapefruit.machine.info.Architecture.X64
import net.rubygrapefruit.machine.info.Machine
import net.rubygrapefruit.strings.capitalized
import org.gradle.kotlin.dsl.support.serviceOf
import java.io.ByteArrayOutputStream
import kotlin.io.path.createDirectories

sealed class Sample(val name: String, val baseDir: File) {
    val dir = baseDir.resolve(name)
}

data class DerivedSrcDir(val origin: File, val target: File)

interface DerivedSample {
    val name: String

    val derivedFrom: Sample

    val derivedSrcDirs: List<DerivedSrcDir>
}

sealed class AppNature {
    abstract fun launcher(launcher: String): AppNature

    abstract fun embedded(): AppNature

    abstract fun native(): AppNature

    abstract val distDirName: String

    abstract val cliLauncherPath: String?

    open val cliLauncherPrefix: List<String>
        get() = emptyList()

    abstract val nativeBinaryPath: String?

    abstract val expectedArchitecture: Architecture?

    abstract val openLauncherPath: String?
}

class JvmLauncherScripts(private val name: String, private val embeddedJvm: Boolean) : AppNature() {
    override fun launcher(launcher: String): AppNature {
        return JvmLauncherScripts(launcher, embeddedJvm)
    }

    override fun embedded(): AppNature {
        return JvmLauncherScripts(name, true)
    }

    override fun native(): AppNature {
        return NativeBinaryCliApp(name, "build/dist", Machine.thisMachine.architecture)
    }

    override val cliLauncherPath: String
        get() = Machine.thisMachine.scriptName(name)

    override val cliLauncherPrefix: List<String>
        get() = if (Machine.thisMachine.isWindows) {
            listOf("cmd", "/C")
        } else {
            emptyList()
        }

    override val distDirName: String
        get() = "build/dist"

    override val nativeBinaryPath = if (embeddedJvm) {
        Machine.thisMachine.executableName("jvm/bin/java")
    } else {
        null
    }

    override val expectedArchitecture = null

    override val openLauncherPath: String?
        get() = null
}

class NativeBinaryCliApp(private val name: String, override val distDirName: String, override val expectedArchitecture: Architecture) : AppNature() {
    override fun launcher(launcher: String): AppNature {
        return NativeBinaryCliApp(launcher, distDirName, expectedArchitecture)
    }

    override fun embedded(): AppNature {
        throw IllegalStateException()
    }

    override fun native(): AppNature {
        return this
    }

    override val cliLauncherPath: String
        get() = Machine.thisMachine.executableName(name)

    override val nativeBinaryPath: String
        get() = cliLauncherPath

    override val openLauncherPath: String?
        get() = null
}

class UiApp(private val appName: String, private val distBaseDir: String, override val expectedArchitecture: Architecture) : AppNature() {
    override fun launcher(launcher: String): AppNature {
        return UiApp(launcher, distBaseDir, expectedArchitecture)
    }

    override fun embedded(): AppNature {
        throw IllegalStateException()
    }

    override fun native(): AppNature {
        return this
    }

    override val distDirName: String
        get() = "${distBaseDir}/${appName}.app"

    override val cliLauncherPath: String?
        get() = null

    override val nativeBinaryPath: String
        get() = "Contents/MacOS/${appName}"

    override val openLauncherPath: String
        get() = nativeBinaryPath
}

data class AppInvocation(val cliArgs: List<String>, val expectedOutput: String?)

class AppDistribution(
    val nature: AppNature,
    val distTaskName: String = "dist"
)

sealed class App(
    name: String,
    baseDir: File,
    val mainDist: AppDistribution,
    val otherDists: List<AppDistribution>,
    val srcDirName: String,
    val allPlatforms: Boolean,
    val invocation: AppInvocation,
) : Sample(name, baseDir) {

    val srcDir = dir.resolve("src/$srcDirName/kotlin")

    val distTask: String
        get() = distTask(mainDist)

    fun distTask(dist: AppDistribution) = ":$name:${dist.distTaskName}"

    val distDir: File
        get() = distDir(mainDist)

    fun distDir(dist: AppDistribution) = dir.resolve(dist.nature.distDirName)

    fun cliLauncher(dist: AppDistribution): File? {
        val cliLauncherPath = dist.nature.cliLauncherPath
        return if (cliLauncherPath != null) {
            distDir(dist).resolve(cliLauncherPath)
        } else {
            null
        }
    }

    fun cliCommandLine(dist: AppDistribution): List<String>? = if (dist.nature.cliLauncherPath != null) {
        dist.nature.cliLauncherPrefix + distDir.resolve(dist.nature.cliLauncherPath!!).absolutePath + invocation.cliArgs
    } else {
        null
    }

    val expectedOutput = invocation.expectedOutput

    fun nativeBinary(dist: AppDistribution): File? {
        val nativeBinaryPath = dist.nature.nativeBinaryPath
        return if (nativeBinaryPath != null) {
            distDir(dist).resolve(nativeBinaryPath)
        } else {
            null
        }
    }

    val openCommandLine = if (mainDist.nature.openLauncherPath != null) {
        listOf("open", distDir.resolve(mainDist.nature.openLauncherPath!!).absolutePath)
    } else {
        null
    }
}

sealed class AppBuilder {
    val cliArgs = mutableListOf<String>()
    var expectedOutput: String? = null

    fun cliArgs(vararg args: String) {
        cliArgs.clear()
        cliArgs.addAll(args)
    }

    fun expectedOutput(text: String) {
        expectedOutput = text
    }

    fun toInvocation() = AppInvocation(cliArgs, expectedOutput)
}

class JvmAppBuilder : AppBuilder()
class NativeAppBuilder : AppBuilder()

sealed class AppNatureBuilder {
    private var launcher: String? = null

    fun launcher(name: String) {
        launcher = name
    }

    open fun build(nature: AppNature): AppNature {
        val launcher = launcher
        return if (launcher != null) {
            nature.launcher(launcher)
        } else {
            nature
        }
    }
}

class JvmAppNatureBuilder : AppNatureBuilder() {
    private var embedded = false
    private var native = false

    fun embedded() {
        embedded = true
    }

    fun native() {
        native = true
    }

    fun jvm(version: Int) {
    }

    override fun build(nature: AppNature): AppNature {
        val nature = super.build(nature)
        return if (embedded) {
            nature.embedded()
        } else if (native) {
            nature.native()
        } else {
            nature
        }
    }
}

class UiAppNatureBuilder : AppNatureBuilder()
class NativeAppNatureBuilder : AppNatureBuilder()

class JvmBaseApp(
    name: String,
    baseDir: File,
    invocation: AppInvocation
) : App(name, baseDir, AppDistribution(JvmLauncherScripts(name, false)), emptyList(), "main", true, invocation) {
    fun derive(suffix: String, config: JvmAppNatureBuilder.() -> Unit): DerivedApp {
        val sampleName = "$name-$suffix"
        val builder = JvmAppNatureBuilder()
        builder.launcher(sampleName)
        builder.config()
        val newNature = builder.build(mainDist.nature)
        return DerivedApp(sampleName, this, baseDir, AppDistribution(newNature), emptyList(), srcDirName, allPlatforms)
    }

    fun deriveNative(name: String): DerivedNativeApp {
        val host = Machine.thisMachine
        val otherDists = if (host.isMacOS && host.architecture == Arm64) {
            listOf(AppDistribution(NativeBinaryCliApp(name, "build/dist-images/macosX64Debug", X64), "macosX64DebugDist"))
        } else {
            emptyList()
        }
        return DerivedNativeApp(name, this, baseDir, AppDistribution(NativeBinaryCliApp(name, "build/dist", host.architecture)), otherDists, "commonMain", false)
    }
}

class NativeBaseApp(
    name: String,
    baseDir: File,
    invocation: AppInvocation
) : App(name, baseDir, AppDistribution(NativeBinaryCliApp(name, "build/dist", Arm64)), emptyList(), "commonMain", true, invocation)

class DerivedNativeApp(
    name: String,
    derivedFrom: App,
    baseDir: File,
    mainDist: AppDistribution,
    otherDists: List<AppDistribution>,
    srcDirName: String,
    allPlatforms: Boolean,
) : DerivedApp(name, derivedFrom, baseDir, mainDist, otherDists, srcDirName, allPlatforms) {
    fun derive(suffix: String, config: NativeAppNatureBuilder.() -> Unit): DerivedNativeApp {
        val builder = NativeAppNatureBuilder()
        builder.config()
        val sampleName = "$name-$suffix"
        val newNature = builder.build(mainDist.nature)
        return DerivedNativeApp(sampleName, derivedFrom, baseDir, AppDistribution(newNature), emptyList(), srcDirName, allPlatforms)
    }
}

class UiBaseApp(
    name: String,
    baseDir: File,
    srcDirName: String,
    mainDist: AppDistribution = AppDistribution(UiApp(name.capitalized(), "build/dist", Machine.thisMachine.architecture)),
    otherDists: List<AppDistribution> = emptyList()
) : App(name, baseDir, mainDist, otherDists, srcDirName, false, AppInvocation(emptyList(), null)) {
    fun derive(suffix: String, config: UiAppNatureBuilder.() -> Unit): DerivedApp {
        val builder = UiAppNatureBuilder()
        builder.config()
        val sampleName = "$name-$suffix"
        val newNature = builder.build(mainDist.nature)
        return DerivedApp(sampleName, this, baseDir, AppDistribution(newNature), emptyList(), srcDirName, allPlatforms)
    }
}

open class DerivedApp(
    name: String,
    override val derivedFrom: App,
    baseDir: File,
    mainDist: AppDistribution,
    otherDists: List<AppDistribution>,
    srcDirName: String,
    allPlatforms: Boolean,
) : App(name, baseDir, mainDist, otherDists, srcDirName, allPlatforms, derivedFrom.invocation), DerivedSample {

    override val derivedSrcDirs: List<DerivedSrcDir>
        get() = listOf(DerivedSrcDir(derivedFrom.srcDir, srcDir))
}

class BaseLib(name: String, baseDir: File, val sourceSets: List<String>) : Sample(name, baseDir) {
    fun derive(suffix: String): DerivedLib {
        val sampleName = "$name-$suffix"
        return DerivedLib(sampleName, this, baseDir)
    }
}

class DerivedLib(name: String, override val derivedFrom: BaseLib, baseDir: File) : Sample(name, baseDir), DerivedSample {
    override val derivedSrcDirs: List<DerivedSrcDir>
        get() = derivedFrom.sourceSets.map { DerivedSrcDir(derivedFrom.dir.resolve("src/$it/kotlin"), dir.resolve("src/$it/kotlin")) }
}

val jvmCliMinApp = jvmCliApp("jvm-cli-app-min") {
    cliArgs("hello", "world")
    expectedOutput("args: hello, world")
}

val jvmCliApp = jvmCliApp("jvm-cli-app") {
    cliArgs("1", "+", "2")
    expectedOutput("Expression: (1) + (2)")
}

val jvmUiApp = jvmUiApp("jvm-ui-app")
val jvmLib = jvmLib("jvm-lib")
val kmpLib = kmpLib("kmp-lib")
val kmpLibRender = kmpLib("kmp-lib-render", listOf("desktopMain", "jvmMain", "mingwMain", "unixMain"))

val nativeCliApp = jvmCliApp.deriveNative("native-cli-app")
val nativeUiApp = macOsUiApp("native-ui-app")

val jvmCliFullApp = jvmCliApp("jvm-cli-app-full") {
    cliArgs("list")
}

val jvmCliStoreApp = jvmCliApp("store-jvm-cli-app") {
    cliArgs("content", "build/test")
}

val samples = listOf(
    jvmLib,
    jvmLib.derive("customized"),

    jvmLib("jvm-lib-generated-source"),

    kmpLib,
    kmpLib.derive("customized"),

    kmpLibRender,
    kmpLibRender.derive("customized"),

    kmpLib("kmp-lib-generated-source"),

    jvmCliApp,
    jvmCliApp.derive("customized") {
        launcher("app")
    },
    jvmCliApp.derive("embedded") {
        embedded()
    },
    jvmCliApp.derive("embedded-customized") {
        embedded()
        launcher("app")
    },
    jvmCliApp.derive("native-binary") {
        native()
    },
    jvmCliApp.derive("native-binary-customized") {
        native()
        launcher("app")
    },
    jvmCliApp.derive("java11") {
        jvm(11)
    },
    jvmCliApp.derive("java25") {
        jvm(24)
    },

    jvmCliMinApp,
    jvmCliFullApp,
    jvmCliStoreApp,
    jvmCliApp("jvm-cli-app-generated-source") {
        expectedOutput("Generated app class")
    },

    jvmUiApp,
    jvmUiApp.derive("customized") {
        launcher("App")
    },

    nativeCliApp,
    nativeCliApp.derive("customized") {
        launcher("app")
    },
    nativeCliApp("native-cli-app-generated-source") {
        expectedOutput("Generated common app class")
    },

    jvmCliMinApp.deriveNative("native-cli-app-min"),
    jvmCliFullApp.deriveNative("native-cli-app-full"),
    jvmCliStoreApp.deriveNative("store-native-cli-app"),

    nativeUiApp,
    nativeUiApp.derive("customized") { launcher("App") },

    jvmCliApp("cli-args-parameters") { cliArgs("--help") },
    jvmCliApp("cli-args-options") { cliArgs("--help") },
    jvmCliApp("cli-args-actions") { cliArgs("--help") }
)

val sampleApps = samples.filterIsInstance<App>()
val derivedSamples = samples.filterIsInstance<DerivedSample>()
val uiApps = sampleApps.filter { it.openCommandLine != null }

val generators = derivedSamples.map { sample ->
    for (srcDir in sample.derivedSrcDirs) {
        if (!srcDir.origin.isDirectory) {
            throw IllegalArgumentException("Missing source directory ${srcDir.origin}")
        }
    }

    tasks.register("generate-${sample.name}") {
        doLast {
            for (srcDir in sample.derivedSrcDirs) {

                srcDir.target.deleteRecursively()

                srcDir.origin.walkTopDown().forEach { file ->
                    val destFile = srcDir.target.resolve(file.relativeTo(srcDir.origin))
                    if (file.isDirectory) {
                        destFile.toPath().createDirectories()
                    } else if (file.isFile) {
                        destFile.parentFile.mkdirs()
                        val text = file.readText()
                        destFile.writeText(text)
                    }
                }
            }
        }
    }
}

tasks.register("generate") {
    dependsOn(generators)
}

val runTasks = sampleApps.associateWith { app ->
    tasks.register("run-${app.name}") {
        val exec = project.serviceOf<ExecOperations>()
        dependsOn(app.distTask)
        doLast {
            Runner().run(app, app.mainDist, exec)
        }
    }
}

val runOtherTasks = sampleApps.map { app ->
    val runTasks = app.otherDists.map { dist ->
        tasks.register("run-${app.name}-${dist.distTaskName}") {
            val exec = project.serviceOf<ExecOperations>()
            dependsOn(app.distTask(dist))
            doLast {
                Runner().run(app, dist, exec)
            }
        }
    }
    tasks.register("run-other-${app.name}") {
        dependsOn(runTasks)
    }
}

val runOther: TaskProvider<Task> = tasks.register("runOther") {
    dependsOn(runOtherTasks)
}

val run: TaskProvider<Task> = tasks.register("run") {
    dependsOn(runTasks.values)
}

tasks.register("runMin") {
    dependsOn(runTasks.filterKeys { it.allPlatforms }.values)
}

val showApplication: TaskProvider<Task> = tasks.register("showApplication") {
    dependsOn(sampleApps.map { ":${it.name}:showApplication" })
}

tasks.register("smokeTest") {
    dependsOn(run, runOther, showApplication)
}

val openTasks = uiApps.map { app ->
    tasks.register("open-${app.name}") {
        val exec = project.serviceOf<ExecOperations>()
        dependsOn(":${app.name}:dist")
        doLast {
            exec.exec {
                commandLine(app.openCommandLine)
            }
        }
    }
}

tasks.register("open") {
    dependsOn(openTasks)
}

class Runner {
    fun run(app: App, dist: AppDistribution, exec: ExecOperations) {
        val distDir = app.distDir(dist)
        println("Dist dir: $distDir")
        if (!distDir.isDirectory) {
            throw IllegalStateException("Application distribution directory $distDir does not exist.")
        }
        val cliLauncher = app.cliLauncher(dist)
        println("CLI launcher: ${cliLauncher?.relativeTo(distDir)}")
        if (cliLauncher != null && !cliLauncher.isFile) {
            throw IllegalStateException("Application launcher $cliLauncher does not exist.")
        }
        val nativeBinary = app.nativeBinary(dist)
        println("Native binary: ${nativeBinary?.relativeTo(distDir)}")
        if (nativeBinary != null && !nativeBinary.isFile) {
            // Can be non-null when CLI launcher is null, for example, embedded JVM app with launcher script
            throw IllegalStateException("Application binary $nativeBinary does not exist.")
        }
        println("Dist size: " + distDir.directorySize().formatSize())
        if (nativeBinary != null && Machine.thisMachine.isMacOS) {
            val str = ByteArrayOutputStream()
            exec.exec {
                commandLine("otool", "-hv", nativeBinary)
                standardOutput = str
            }
            val arch = str.toString().lines()[3].split(Regex("\\s+"))[1]
            println("Binary: $arch")
            val expected = when (dist.nature.expectedArchitecture) {
                X64 -> "X86_64"
                Arm64 -> "ARM64"
                null -> null
            }

            if (expected != null && arch != expected) {
                throw IllegalStateException("Unexpected binary architecture: $arch, expected: $expected")
            }
        }
        val cliCommandLine = app.cliCommandLine(dist)
        if (cliCommandLine != null) {
            val str = ByteArrayOutputStream()
            exec.exec {
                commandLine(cliCommandLine)
                standardOutput = str
            }
            println()
            println("----")
            print(str)
            println("----")
            if (app.expectedOutput != null && !str.toString().contains(app.expectedOutput)) {
                throw IllegalStateException("Unexpected application output")
            }
        } else {
            println()
            println("(no CLI)")
        }
    }

    fun File.directorySize(): Long {
        var size: Long = 0
        for (file in this.walkBottomUp()) {
            if (file.isFile) {
                size += file.length()
            }
        }
        return size
    }

    fun Long.formatSize(): String {
        val mb = this.toBigDecimal().setScale(2) / (1000 * 1000).toBigDecimal()
        return "$mb MB"
    }
}

fun jvmCliApp(name: String, config: JvmAppBuilder.() -> Unit = {}): JvmBaseApp {
    val builder = JvmAppBuilder()
    builder.config()
    return JvmBaseApp(name, projectDir, builder.toInvocation())
}

fun jvmUiApp(name: String) = UiBaseApp(name, projectDir, "main")

fun nativeCliApp(name: String, config: NativeAppBuilder.() -> Unit = {}): NativeBaseApp {
    val builder = NativeAppBuilder()
    builder.config()
    return NativeBaseApp(name, projectDir, builder.toInvocation())
}

fun macOsUiApp(name: String): UiBaseApp {
    val host = Machine.thisMachine
    val otherDists = if (host.isMacOS && host.architecture == Arm64) {
        listOf(AppDistribution(UiApp(name, "build/dist-images/macosX64Debug", X64), "macosX64DebugDist"))
    } else {
        emptyList()
    }

    return UiBaseApp(name, projectDir, "macosMain", otherDists = otherDists)
}

fun jvmLib(name: String) = BaseLib(name, projectDir, listOf("main"))

fun kmpLib(name: String, sourceSets: List<String> = listOf("commonMain")) = BaseLib(name, projectDir, sourceSets)
