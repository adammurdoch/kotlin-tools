import net.rubygrapefruit.machine.info.Machine
import org.jetbrains.kotlin.incremental.createDirectory
import java.io.ByteArrayOutputStream

sealed class Sample(val name: String, val baseDir: File, val srcDirName: String) {
    val dir = baseDir.resolve(name)

    val srcDir = dir.resolve("src/$srcDirName/kotlin")
}

interface DerivedSample {
    val name: String

    val derivedFrom: Sample

    val srcDir: File

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

    abstract val openLauncherPath: String?
}

class JvmCliApp(private val name: String, private val embeddedJvm: Boolean) : AppNature() {
    override fun launcher(launcher: String): AppNature {
        return JvmCliApp(launcher, embeddedJvm)
    }

    override fun embedded(): AppNature {
        return JvmCliApp(name, true)
    }

    override fun native(): AppNature {
        return NativeBinaryCliApp(name)
    }

    override val cliLauncherPath: String
        get() = if (Machine.thisMachine is Machine.Windows) {
            "$name.bat"
        } else {
            name
        }

    override val cliLauncherPrefix: List<String>
        get() = if (Machine.thisMachine is Machine.Windows) {
            listOf("cmd", "/C")
        } else {
            emptyList()
        }

    override val distDirName: String
        get() = "build/dist-image"

    override val nativeBinaryPath = if (embeddedJvm) {
        if (Machine.thisMachine is Machine.Windows) {
            "jvm/bin/java.exe"
        } else {
            "jvm/bin/java"
        }
    } else {
        null
    }

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
        get() = "build/dist-image"

    override val cliLauncherPath: String
        get() = if (Machine.thisMachine is Machine.Windows) {
            "$name.exe"
        } else {
            name
        }

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
        get() = "build/dist-image/${appName}.app"

    override val cliLauncherPath: String?
        get() = null

    override val nativeBinaryPath: String
        get() = "Contents/MacOS/${appName}"

    override val openLauncherPath: String
        get() = nativeBinaryPath
}

sealed class App(
    name: String,
    baseDir: File,
    val nature: AppNature,
    srcDirName: String,
    val includePackages: Boolean,
    val allPlatforms: Boolean,
    protected val cliArgs: List<String>
) :
    Sample(name, baseDir, srcDirName) {

    val distTask = ":$name:dist"

    val distDir = dir.resolve(nature.distDirName)

    val cliLauncher = if (nature.cliLauncherPath != null) {
        distDir.resolve(nature.cliLauncherPath!!)
    } else {
        null
    }

    val cliCommandLine = if (nature.cliLauncherPath != null) {
        nature.cliLauncherPrefix + distDir.resolve(nature.cliLauncherPath!!) + cliArgs
    } else {
        null
    }

    val nativeBinary = if (nature.nativeBinaryPath != null) {
        distDir.resolve(nature.nativeBinaryPath!!)
    } else {
        null
    }

    val openCommandLine = if (nature.openLauncherPath != null) {
        listOf("open", distDir.resolve(nature.openLauncherPath!!).absolutePath)
    } else {
        null
    }

    abstract val derivedFrom: BaseApp

    fun derive(suffix: String, builder: (AppNature) -> AppNature = { it }): DerivedApp {
        val sampleName = "$name-$suffix"
        return DerivedApp(sampleName, derivedFrom, baseDir, builder(nature.launcher(sampleName)), srcDirName, includePackages)
    }
}

class BaseApp(
    name: String,
    baseDir: File,
    nature: AppNature,
    srcDirName: String,
    includePackages: Boolean,
    allPlatforms: Boolean = false,
    cliArgs: List<String> = listOf("1", "+", "2")
) :
    App(name, baseDir, nature, srcDirName, includePackages, allPlatforms, cliArgs) {

    override val derivedFrom: BaseApp
        get() = this

    fun deriveNative(name: String): DerivedApp {
        return DerivedApp(name, derivedFrom, baseDir, NativeBinaryCliApp(name), "commonMain", false)
    }

    fun allPlatforms(): BaseApp {
        return BaseApp(name, baseDir, nature, srcDirName, includePackages, true, cliArgs)
    }

    fun cliArgs(vararg args: String): BaseApp {
        return BaseApp(name, baseDir, nature, srcDirName, includePackages, allPlatforms, args.toList())
    }
}

class DerivedApp(
    name: String,
    override val derivedFrom: BaseApp,
    baseDir: File,
    nature: AppNature,
    srcDirName: String,
    includePackages: Boolean,
    allPlatforms: Boolean = false,
    cliArgs: List<String> = listOf("1", "+", "2")
) :
    App(name, baseDir, nature, srcDirName, includePackages, allPlatforms, cliArgs), DerivedSample {

    fun allPlatforms(): DerivedApp {
        return DerivedApp(name, derivedFrom, baseDir, nature, srcDirName, includePackages, true, cliArgs)
    }
}

class BaseLib(name: String, baseDir: File, srcDirName: String) : Sample(name, baseDir, srcDirName) {
    fun derive(suffix: String): DerivedLib {
        val sampleName = "$name-$suffix"
        return DerivedLib(sampleName, this, baseDir, srcDirName)
    }
}

class DerivedLib(name: String, override val derivedFrom: BaseLib, baseDir: File, srcDirName: String) :
    Sample(name, baseDir, srcDirName), DerivedSample {

    override val includePackages: Boolean
        get() = true
}

val jvmCliApp = jvmCliApp("jvm-cli-app")
val jvmUiApp = jvmUiApp("jvm-ui-app")
val jvmLib = jvmLib("jvm-lib")
val kmpLib = mppLib("kmp-lib")
val nativeCliApp = jvmCliApp.deriveNative("native-cli-app")
val nativeUiApp = macOsUiApp("native-ui-app")

val samples = listOf(
    jvmLib,
    jvmLib.derive("customized"),

    kmpLib,
    kmpLib.derive("customized"),

    jvmCliApp.allPlatforms(),
    jvmCliApp.derive("customized") { it.launcher("app") }.allPlatforms(),
    jvmCliApp.derive("embedded") { it.embedded() }.allPlatforms(),
    jvmCliApp.derive("embedded-customized") { it.embedded().launcher("app") }.allPlatforms(),
    jvmCliApp.derive("native-binary") { it.native() }.allPlatforms(),
    jvmCliApp.derive("native-binary-customized") { it.native().launcher("app") }.allPlatforms(),

    jvmCliApp("jvm-cli-app-min").cliArgs("hello", "world").allPlatforms(),
    jvmCliApp("jvm-cli-app-full").cliArgs("list").allPlatforms(),
    jvmCliApp("jvm-store-cli-app").cliArgs("content", "build/test").allPlatforms(),

    jvmUiApp,
    jvmUiApp.derive("customized") { it.launcher("App") },

    nativeCliApp.allPlatforms(),
    nativeCliApp.derive("customized") { it.launcher("app") }.allPlatforms(),

    nativeCliApp("native-cli-app-min").cliArgs("hello", "world").allPlatforms(),
    nativeCliApp("native-cli-app-full").cliArgs("list").allPlatforms(),

    nativeUiApp,
    nativeUiApp.derive("customized") { it.launcher("App") }
)

val sampleApps = samples.filterIsInstance<App>()
val derivedSamples = samples.filterIsInstance<DerivedSample>()
val uiApps = sampleApps.filter { it.openCommandLine != null }

val generators = derivedSamples.map { sample ->
    val originDir = sample.derivedFrom.srcDir
    if (!originDir.isDirectory) {
        throw IllegalArgumentException("Missing source directory $originDir")
    }

    tasks.register("generate-${sample.name}") {
        doLast {
            sample.srcDir.deleteRecursively()

            originDir.walkTopDown().forEach { file ->
                val destFile = if (sample.includePackages) {
                    sample.srcDir.resolve(file.relativeTo(originDir))
                } else {
                    sample.srcDir.resolve(file.name)
                }
                if (file.isDirectory && sample.includePackages) {
                    destFile.createDirectory()
                } else if (file.isFile) {
                    destFile.parentFile.mkdirs()
                    val text = file.readText()
                    if (sample.includePackages) {
                        destFile.writeText(text)
                    } else {
                        destFile.writeText(text.lines().drop(2).joinToString("\n"))
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
            if (app.nativeBinary != null && Machine.thisMachine is Machine.MacOS) {
                val str = ByteArrayOutputStream()
                exec {
                    commandLine("otool", "-hv", app.nativeBinary)
                    standardOutput = str
                }
                println("binary: ${str.toString().lines()[3].split(Regex("\\s+"))[1]}")
            }
            if (app.cliCommandLine != null) {
                println()
                println("----")
                exec {
                    commandLine(app.cliCommandLine)
                }
                println("----")
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

fun jvmCliApp(name: String): BaseApp {
    return BaseApp(name, projectDir, JvmCliApp(name, false), "main", true)
}

fun jvmUiApp(name: String) = BaseApp(name, projectDir, UiApp(name.capitalize()), "main", true)

fun nativeCliApp(name: String): BaseApp {
    return BaseApp(name, projectDir, NativeBinaryCliApp(name), "commonMain", false)
}

fun macOsUiApp(name: String) = BaseApp(name, projectDir, UiApp(name.capitalize()), "macosMain", true)

fun jvmLib(name: String) = BaseLib(name, projectDir, "main")

fun mppLib(name: String) = BaseLib(name, projectDir, "commonMain")

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