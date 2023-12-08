import net.rubygrapefruit.machine.info.Machine
import java.io.ByteArrayOutputStream

sealed class Sample(val name: String, val baseDir: File, val srcDirName: String) {
    val dir = baseDir.resolve(name)

    val srcDir = dir.resolve("src/$srcDirName/kotlin")
}

interface DerivedSample {
    val name: String

    val derivedFrom: Sample

    val srcDir: File
}

sealed class AppNature {
    abstract fun launcher(launcher: String): AppNature

    abstract fun embedded(): AppNature

    abstract fun native(): AppNature

    abstract val distDirName: String

    abstract val cliLauncherPath: String?

    open val launcherCommand: List<String>
        get() = emptyList()

    abstract val nativeBinaryPath: String?
}

class JvmCliApp(private val name: String, private val embeddedJvm: Boolean) : AppNature() {
    override fun launcher(launcher: String): AppNature {
        return JvmCliApp(launcher, embeddedJvm)
    }

    override fun embedded(): AppNature {
        return JvmCliApp(name, true)
    }

    override val cliLauncherPath: String
        get() = if (Machine.thisMachine is Machine.Windows) {
            "$name.bat"
        } else {
            name
        }

    override val launcherCommand: List<String>
        get() = if (Machine.thisMachine is Machine.Windows) {
            listOf("cmd", "/C")
        } else {
            emptyList()
        }

    override fun native(): AppNature {
        return NativeBinaryCliApp(name)
    }

    override val distDirName: String
        get() = "build/dist-image"

    override val nativeBinaryPath = if (embeddedJvm) {
        "jvm/bin/java"
    } else {
        null
    }
}

class NativeBinaryCliApp(override val cliLauncherPath: String) : AppNature() {
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


    override val nativeBinaryPath: String
        get() = cliLauncherPath
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
        get() = "build/debug/${appName}.app"

    override val cliLauncherPath: String?
        get() = null

    override val nativeBinaryPath: String
        get() = "Contents/MacOS/${appName}"
}

sealed class App(name: String, baseDir: File, val nature: AppNature, srcDirName: String, val allPlatforms: Boolean) :
    Sample(name, baseDir, srcDirName) {

    val distDir = dir.resolve(nature.distDirName)

    val cliLauncher = if (nature.cliLauncherPath != null) {
        distDir.resolve(nature.cliLauncherPath!!)
    } else {
        null
    }

    val commandLine = if (nature.cliLauncherPath != null) {
        nature.launcherCommand + distDir.resolve(nature.cliLauncherPath!!)
    } else {
        null
    }

    val nativeBinary = if (nature.nativeBinaryPath != null) {
        distDir.resolve(nature.nativeBinaryPath!!)
    } else {
        null
    }
}

class BaseApp(name: String, baseDir: File, nature: AppNature, srcDirName: String, allPlatforms: Boolean = false) :
    App(name, baseDir, nature, srcDirName, allPlatforms) {

    fun derive(suffix: String, builder: (AppNature) -> AppNature = { it }): DerivedApp {
        val sampleName = "$name-$suffix"
        return DerivedApp(sampleName, this, baseDir, builder(nature.launcher(sampleName)))
    }

    fun allPlatforms(): BaseApp {
        return BaseApp(name, baseDir, nature, srcDirName, true)
    }
}

class DerivedApp(name: String, override val derivedFrom: BaseApp, baseDir: File, nature: AppNature) :
    App(name, baseDir, nature, derivedFrom.srcDirName, false), DerivedSample

class BaseLib(name: String, baseDir: File, srcDirName: String) : Sample(name, baseDir, srcDirName) {
    fun derive(suffix: String): DerivedLib {
        val sampleName = "$name-$suffix"
        return DerivedLib(sampleName, this, baseDir, srcDirName)
    }
}

class DerivedLib(name: String, override val derivedFrom: BaseLib, baseDir: File, srcDirName: String) :
    Sample(name, baseDir, srcDirName), DerivedSample

val jvmCliApp = jvmCliApp("jvm-cli-app").allPlatforms()
val jvmUiApp = jvmUiApp("jvm-ui-app")
val jvmLib = jvmLib("jvm-lib")
val kmpLib = mppLib("kmp-lib")
val nativeCliApp = nativeCliApp("native-cli-app").allPlatforms()
val nativeUiApp = macOsUiApp("native-ui-app")

val samples = listOf(
    jvmLib,
    jvmLib.derive("customized"),

    kmpLib,
    kmpLib.derive("customized"),

    jvmCliApp,
    jvmCliApp.derive("customized") { it.launcher("app") },
    jvmCliApp.derive("embedded") { it.embedded() },
    jvmCliApp.derive("embedded-customized") { it.embedded().launcher("app") },
    jvmCliApp.derive("native-binary") { it.native() },
    jvmCliApp.derive("native-binary-customized") { it.native().launcher("app") },

    jvmUiApp,
    jvmUiApp.derive("customized") { it.launcher("App") },

    nativeCliApp,
    nativeCliApp.derive("customized") { it.launcher("app") },

    nativeUiApp,
    nativeUiApp.derive("customized") { it.launcher("App") }
)

val sampleApps = samples.filterIsInstance<App>()
val derivedSamples = samples.filterIsInstance<DerivedSample>()

val generators = derivedSamples.map { sample ->
    if (!sample.derivedFrom.srcDir.isDirectory) {
        throw IllegalArgumentException("Missing source directory ${sample.derivedFrom.srcDir}")
    }

    tasks.register("generate-${sample.name}") {
        doLast {
            sync {
                from(sample.derivedFrom.srcDir)
                into(sample.srcDir)
            }
        }
    }
}

tasks.register("generate") {
    dependsOn(generators)
}

val runTasks = sampleApps.associateWith { app ->
    tasks.register("run-${app.name}") {
        dependsOn(":${app.name}:dist")
        doLast {
            if (!app.distDir.isDirectory) {
                throw IllegalStateException("Application distribution directory does not exist.")
            }
            if (app.cliLauncher != null && !app.cliLauncher.isFile) {
                throw IllegalStateException("Application launcher does not exist.")
            }
            if (app.nativeBinary != null && app.nativeBinary != app.cliLauncher && !app.nativeBinary.isFile) {
                throw IllegalStateException("Application binary does not exist.")
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
            if (app.commandLine != null) {
                println("-> APP CLI: ${app.commandLine}")
                println()
                println("----")
                exec {
                    commandLine(app.commandLine + "1 + 2")
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

fun jvmCliApp(name: String): BaseApp {
    return BaseApp(name, projectDir, JvmCliApp(name, false), "main")
}

fun jvmUiApp(name: String) = BaseApp(name, projectDir, UiApp(name.capitalize()), "main")

fun nativeCliApp(name: String): BaseApp {
    val launcher = if (Machine.thisMachine is Machine.Windows) {
        "$name.exe"
    } else {
        name
    }
    return BaseApp(name, projectDir, NativeBinaryCliApp(launcher), "commonMain")
}

fun macOsUiApp(name: String) = BaseApp(name, projectDir, UiApp(name.capitalize()), "macosMain")

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