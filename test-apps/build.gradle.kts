import net.rubygrapefruit.machine.info.Architecture
import net.rubygrapefruit.machine.info.Architecture.Arm64
import net.rubygrapefruit.machine.info.Architecture.X64
import net.rubygrapefruit.machine.info.Machine
import net.rubygrapefruit.strings.capitalized
import org.jetbrains.kotlin.incremental.createDirectory
import java.io.ByteArrayOutputStream

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
) :
    Sample(name, baseDir) {

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

    abstract val derivedFrom: BaseApp

    fun derive(suffix: String, builder: (AppNature) -> AppNature = { it }): DerivedApp {
        val sampleName = "$name-$suffix"
        val newNature = builder(mainDist.nature.launcher(sampleName))
        return DerivedApp(sampleName, derivedFrom, baseDir, AppDistribution(newNature), emptyList(), srcDirName, allPlatforms)
    }
}

open class BaseApp(
    name: String,
    baseDir: File,
    mainDist: AppDistribution,
    otherDists: List<AppDistribution>,
    srcDirName: String,
    includePackages: Boolean,
    invocation: AppInvocation = AppInvocation(listOf("1", "+", "2"), "Expression: (1) + (2)")
) :
    App(name, baseDir, mainDist, otherDists, srcDirName, includePackages, invocation) {

    override val derivedFrom: BaseApp
        get() = this

    fun deriveNative(name: String): DerivedApp {
        val host = Machine.thisMachine
        val otherDists = if (host.isMacOS && host.architecture == Arm64) {
            listOf(AppDistribution(NativeBinaryCliApp(name, "build/dist-images/macosX64Debug", X64), "macosX64DebugDist"))
        } else {
            emptyList()
        }
        return DerivedApp(name, derivedFrom, baseDir, AppDistribution(NativeBinaryCliApp(name, "build/dist", host.architecture)), otherDists, "commonMain", false)
    }

    fun allPlatforms(): BaseApp {
        return BaseApp(name, baseDir, mainDist, otherDists, srcDirName, true, invocation)
    }

    fun cliArgs(vararg args: String): BaseApp {
        return BaseApp(name, baseDir, mainDist, otherDists, srcDirName, allPlatforms, AppInvocation(args.toList(), null))
    }
}

class JvmBaseApp(
    name: String,
    baseDir: File,
) : BaseApp(name, baseDir, AppDistribution(JvmLauncherScripts(name, false)), emptyList(), "main", true)

class UiBaseApp(
    name: String,
    baseDir: File,
    srcDirName: String,
    mainDist: AppDistribution = AppDistribution(UiApp(name.capitalized(), "build/dist", Machine.thisMachine.architecture)),
    otherDists: List<AppDistribution> = emptyList()
) : BaseApp(name, baseDir, mainDist, otherDists, srcDirName, true)

class DerivedApp(
    name: String,
    override val derivedFrom: BaseApp,
    baseDir: File,
    mainDist: AppDistribution,
    otherDists: List<AppDistribution>,
    srcDirName: String,
    allPlatforms: Boolean,
) :
    App(name, baseDir, mainDist, otherDists, srcDirName, allPlatforms, derivedFrom.invocation), DerivedSample {

    fun allPlatforms(): DerivedApp {
        return DerivedApp(name, derivedFrom, baseDir, mainDist, otherDists, srcDirName, true)
    }

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

val jvmCliMinApp = jvmCliApp("jvm-cli-app-min").cliArgs("hello", "world")

val jvmCliApp = jvmCliApp("jvm-cli-app")
val jvmUiApp = jvmUiApp("jvm-ui-app")
val jvmLib = jvmLib("jvm-lib")
val kmpLib = kmpLib("kmp-lib")
val kmpLibRender = kmpLib("kmp-lib-render", listOf("desktopMain", "jvmMain", "mingwMain", "unixMain"))

val nativeCliApp = jvmCliApp.deriveNative("native-cli-app")
val nativeUiApp = macOsUiApp("native-ui-app")

val jvmCliFullApp = jvmCliApp("jvm-cli-app-full").cliArgs("list")

val jvmCliStoreApp = jvmCliApp("store-jvm-cli-app").cliArgs("content", "build/test")

val samples = listOf(
    jvmLib,
    jvmLib.derive("customized"),

    jvmLib("jvm-lib-generated-source"),

    kmpLib,
    kmpLib.derive("customized"),

    kmpLibRender,
    kmpLibRender.derive("customized"),

    jvmCliApp.allPlatforms(),
    jvmCliApp.derive("customized") { it.launcher("app") }.allPlatforms(),
    jvmCliApp.derive("embedded") { it.embedded() }.allPlatforms(),
    jvmCliApp.derive("embedded-customized") { it.embedded().launcher("app") }.allPlatforms(),
    jvmCliApp.derive("native-binary") { it.native() }.allPlatforms(),
    jvmCliApp.derive("native-binary-customized") { it.native().launcher("app") }.allPlatforms(),

    jvmCliMinApp.allPlatforms(),
    jvmCliFullApp.allPlatforms(),
    jvmCliStoreApp.allPlatforms(),
    jvmCliApp("jvm-cli-app-generated-source").cliArgs().allPlatforms(),

    jvmUiApp,
    jvmUiApp.derive("customized") { it.launcher("App") },

    nativeCliApp.allPlatforms(),
    nativeCliApp.derive("customized") { it.launcher("app") }.allPlatforms(),

    jvmCliMinApp.deriveNative("native-cli-app-min").allPlatforms(),
    jvmCliFullApp.deriveNative("native-cli-app-full").allPlatforms(),
    jvmCliStoreApp.deriveNative("store-native-cli-app").allPlatforms(),

    nativeUiApp,
    nativeUiApp.derive("customized") { it.launcher("App") },

    jvmCliApp("cli-args-test").cliArgs("--help").allPlatforms()
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
                        destFile.createDirectory()
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
        dependsOn(app.distTask)
        doLast {
            run(app, app.mainDist)
        }
    }
}

val runOtherTasks = sampleApps.flatMap { app ->
    app.otherDists.map { dist ->
        tasks.register("run-${app.name}-${dist.distTaskName}") {
            dependsOn(app.distTask(dist))
            doLast {
                run(app, dist)
            }
        }
    }
}

tasks.register("runOther") {
    dependsOn(runOtherTasks)
}

tasks.register("run") {
    dependsOn(runTasks.values)
}

tasks.register("runMin") {
    dependsOn(runTasks.filterKeys { it.allPlatforms }.values)
}

tasks.register("showDistributions") {
    dependsOn(sampleApps.map { ":${it.name}:showDistributions" })
}

val openTasks = uiApps.map { app ->
    tasks.register("open-${app.name}") {
        dependsOn(":${app.name}:dist")
        doLast {
            exec {
                commandLine(app.openCommandLine)
            }
        }
    }
}

tasks.register("open") {
    dependsOn(openTasks)
}

fun run(app: App, dist: AppDistribution) {
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
        exec {
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
        exec {
            commandLine(cliCommandLine)
            standardOutput = str
        }
        println()
        println("----")
        println(str)
        println("----")
        if (app.expectedOutput != null && !str.toString().contains(app.expectedOutput)) {
            throw IllegalStateException("Unexpected application output")
        }
    } else {
        println()
        println("(no CLI)")
    }
}

fun jvmCliApp(name: String): JvmBaseApp {
    return JvmBaseApp(name, projectDir)
}

fun jvmUiApp(name: String) = UiBaseApp(name, projectDir, "main")

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