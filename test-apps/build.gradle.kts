import net.rubygrapefruit.machine.info.Architecture.Arm64
import net.rubygrapefruit.machine.info.Architecture.X64
import net.rubygrapefruit.machine.info.Machine
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

    val includePackages: Boolean
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

    open val expectedArchitecture: String?
        get() {
            return if (nativeBinaryPath == null) {
                null
            } else when (Machine.thisMachine.architecture) {
                X64 -> "x86_64"
                Arm64 -> "ARM64"
            }
        }

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
        return NativeBinaryCliApp(name)
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

class NativeBinaryCliApp(private val name: String) : AppNature() {
    override fun launcher(launcher: String): AppNature {
        return NativeBinaryCliApp(launcher)
    }

    override fun embedded(): AppNature {
        throw IllegalStateException()
    }

    override fun native(): AppNature {
        return this
    }

    override val distDirName: String
        get() = "build/dist"

    override val cliLauncherPath: String
        get() = Machine.thisMachine.executableName(name)

    override val nativeBinaryPath: String
        get() = cliLauncherPath

    override val openLauncherPath: String?
        get() = null
}

class UiApp(private val appName: String) : AppNature() {
    override fun launcher(launcher: String): AppNature {
        return UiApp(launcher)
    }

    override fun embedded(): AppNature {
        throw IllegalStateException()
    }

    override fun native(): AppNature {
        return this
    }

    override val distDirName: String
        get() = "build/dist/${appName}.app"

    override val cliLauncherPath: String?
        get() = null

    override val nativeBinaryPath: String
        get() = "Contents/MacOS/${appName}"

    override val openLauncherPath: String
        get() = nativeBinaryPath
}

data class AppInvocation(val cliArgs: List<String>, val expectedOutput: String?)

class AppDistribution(
    val nature: AppNature
)

sealed class App(
    name: String,
    baseDir: File,
    val mainDist: AppDistribution,
    val srcDirName: String,
    val includePackages: Boolean,
    val allPlatforms: Boolean,
    val invocation: AppInvocation,
) :
    Sample(name, baseDir) {

    val srcDir = dir.resolve("src/$srcDirName/kotlin")

    val distTask = ":$name:dist"

    val distDir = dir.resolve(mainDist.nature.distDirName)

    val cliLauncher = if (mainDist.nature.cliLauncherPath != null) {
        distDir.resolve(mainDist.nature.cliLauncherPath!!)
    } else {
        null
    }

    val cliCommandLine = if (mainDist.nature.cliLauncherPath != null) {
        mainDist.nature.cliLauncherPrefix + distDir.resolve(mainDist.nature.cliLauncherPath!!) + invocation.cliArgs
    } else {
        null
    }

    val expectedOutput = invocation.expectedOutput

    val nativeBinary = if (mainDist.nature.nativeBinaryPath != null) {
        distDir.resolve(mainDist.nature.nativeBinaryPath!!)
    } else {
        null
    }

    val expectedArchitecture = mainDist.nature.expectedArchitecture

    val openCommandLine = if (mainDist.nature.openLauncherPath != null) {
        listOf("open", distDir.resolve(mainDist.nature.openLauncherPath!!).absolutePath)
    } else {
        null
    }

    abstract val derivedFrom: BaseApp

    fun derive(suffix: String, builder: (AppNature) -> AppNature = { it }): DerivedApp {
        val sampleName = "$name-$suffix"
        val newNature = builder(mainDist.nature.launcher(sampleName))
        return DerivedApp(sampleName, derivedFrom, baseDir, AppDistribution(newNature), emptyList(), srcDirName, includePackages, allPlatforms)
    }
}

open class BaseApp(
    name: String,
    baseDir: File,
    mainDist: AppDistribution,
    srcDirName: String,
    includePackages: Boolean,
    allPlatforms: Boolean = false,
    invocation: AppInvocation = AppInvocation(listOf("1", "+", "2"), "Expression: (1) + (2)")
) :
    App(name, baseDir, mainDist, srcDirName, includePackages, allPlatforms, invocation) {

    override val derivedFrom: BaseApp
        get() = this

    fun deriveNative(name: String): DerivedApp {
        return DerivedApp(name, derivedFrom, baseDir, AppDistribution(NativeBinaryCliApp(name)), emptyList(), "commonMain", false, allPlatforms)
    }

    fun allPlatforms(): BaseApp {
        return BaseApp(name, baseDir, mainDist, srcDirName, includePackages, true, invocation)
    }

    fun cliArgs(vararg args: String): BaseApp {
        return BaseApp(name, baseDir, mainDist, srcDirName, includePackages, allPlatforms, AppInvocation(args.toList(), null))
    }
}

class JvmBaseApp(
    name: String,
    baseDir: File,
) : BaseApp(name, baseDir, AppDistribution(JvmLauncherScripts(name, false)), "main", true)

class UiBaseApp(
    name: String,
    baseDir: File,
    srcDirName: String
) : BaseApp(name, baseDir, AppDistribution(UiApp(name.capitalize())), srcDirName, true)

class DerivedApp(
    name: String,
    override val derivedFrom: BaseApp,
    baseDir: File,
    mainDist: AppDistribution,
    val otherDists: List<AppDistribution>,
    srcDirName: String,
    includePackages: Boolean,
    allPlatforms: Boolean,
) :
    App(name, baseDir, mainDist, srcDirName, includePackages, allPlatforms, derivedFrom.invocation), DerivedSample {

    fun allPlatforms(): DerivedApp {
        return DerivedApp(name, derivedFrom, baseDir, mainDist, otherDists, srcDirName, includePackages, true)
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

    override val includePackages: Boolean
        get() = true

    override val derivedSrcDirs: List<DerivedSrcDir>
        get() = derivedFrom.sourceSets.map { DerivedSrcDir(derivedFrom.dir.resolve("src/$it/kotlin"), dir.resolve("src/$it/kotlin")) }
}

val jvmCliMinApp = jvmCliApp("jvm-cli-app-min").cliArgs("hello", "world")

val jvmCliApp = jvmCliApp("jvm-cli-app")
val jvmUiApp = jvmUiApp("jvm-ui-app")
val jvmLib = jvmLib("jvm-lib")
val kmpLib = mppLib("kmp-lib")
val kmpLibRender = mppLib("kmp-lib-render", listOf("desktopMain", "jvmMain", "mingwMain", "unixMain"))

val nativeCliApp = jvmCliApp.deriveNative("native-cli-app")
val nativeUiApp = macOsUiApp("native-ui-app")

val jvmCliFullApp = jvmCliApp("jvm-cli-app-full").cliArgs("list")

val jvmCliStoreApp = jvmCliApp("store-jvm-cli-app").cliArgs("content", "build/test")

val samples = listOf(
    jvmLib,
    jvmLib.derive("customized"),

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

    jvmUiApp,
    jvmUiApp.derive("customized") { it.launcher("App") },

    nativeCliApp.allPlatforms(),
    nativeCliApp.derive("customized") { it.launcher("app") }.allPlatforms(),

    jvmCliMinApp.deriveNative("native-cli-app-min").allPlatforms(),
    jvmCliFullApp.deriveNative("native-cli-app-full").allPlatforms(),
    jvmCliStoreApp.deriveNative("store-native-cli-app").allPlatforms(),

    nativeUiApp,
    nativeUiApp.derive("customized") { it.launcher("App") }
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
                    val destFile = if (sample.includePackages) {
                        srcDir.target.resolve(file.relativeTo(srcDir.origin))
                    } else {
                        srcDir.target.resolve(file.name)
                    }
                    if (file.isDirectory && sample.includePackages) {
                        destFile.createDirectory()
                    } else if (file.isFile) {
                        destFile.parentFile.mkdirs()
                        val text = file.readText()
                        if (sample.includePackages) {
                            destFile.writeText(text)
                        } else {
                            val lines = text.lines()
                            val packageIndex = lines.indexOfFirst { it.trim().startsWith("package ") }
                            val modified = lines.subList(0, packageIndex) + lines.subList(packageIndex + 2, lines.size)
                            destFile.writeText(modified.joinToString("\n"))
                        }
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
            if (!app.distDir.isDirectory) {
                throw IllegalStateException("Application distribution directory ${app.distDir} does not exist.")
            }
            if (app.cliLauncher != null && !app.cliLauncher.isFile) {
                throw IllegalStateException("Application launcher ${app.cliLauncher} does not exist.")
            }
            if (app.nativeBinary != null && !app.nativeBinary.isFile) {
                // For example, embedded JVM app with launcher script, UI apps
                throw IllegalStateException("Application binary ${app.nativeBinary} does not exist.")
            }
            println("dist size: " + app.distDir.directorySize().formatSize())
            if (app.nativeBinary != null && Machine.thisMachine.isMacOS) {
                val str = ByteArrayOutputStream()
                exec {
                    commandLine("otool", "-hv", app.nativeBinary)
                    standardOutput = str
                }
                val arch = str.toString().lines()[3].split(Regex("\\s+"))[1]
                println("binary: $arch")
                val expected = app.expectedArchitecture
                if (expected != null && arch != expected) {
                    throw IllegalStateException("Unexpected binary architecture")
                }
            }
            if (app.cliCommandLine != null) {
                val str = ByteArrayOutputStream()
                exec {
                    commandLine(app.cliCommandLine)
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
                println("(UI app)")
            }
        }
    }
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

fun jvmCliApp(name: String): JvmBaseApp {
    return JvmBaseApp(name, projectDir)
}

fun jvmUiApp(name: String) = UiBaseApp(name, projectDir, "main")

fun macOsUiApp(name: String) = UiBaseApp(name, projectDir, "macosMain")

fun jvmLib(name: String) = BaseLib(name, projectDir, listOf("main"))

fun mppLib(name: String, sourceSets: List<String> = listOf("commonMain")) = BaseLib(name, projectDir, sourceSets)

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