import java.io.PrintWriter

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

    abstract val nativeBinaryPath: String?
}

class JvmCliApp(override val cliLauncherPath: String, private val embeddedJvm: Boolean) : AppNature() {
    override fun launcher(launcher: String): AppNature {
        return JvmCliApp(launcher, embeddedJvm)
    }

    override fun embedded(): AppNature {
        return JvmCliApp(cliLauncherPath, true)
    }

    override fun native(): AppNature {
        return NativeBinaryCliApp(cliLauncherPath)
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

sealed class App(name: String, baseDir: File, val nature: AppNature, srcDirName: String) :
    Sample(name, baseDir, srcDirName) {

    val distDir = dir.resolve(nature.distDirName)

    val cliLauncher = if (nature.cliLauncherPath != null) {
        distDir.resolve(nature.cliLauncherPath!!)
    } else {
        null
    }

    val nativeBinary = if (nature.nativeBinaryPath != null) {
        distDir.resolve(nature.nativeBinaryPath!!)
    } else {
        null
    }
}

class BaseApp(name: String, baseDir: File, nature: AppNature, srcDirName: String) :
    App(name, baseDir, nature, srcDirName) {

    fun derive(suffix: String, builder: (AppNature) -> AppNature = { it }): DerivedApp {
        val sampleName = "$name-$suffix"
        return DerivedApp(sampleName, this, baseDir, builder(nature.launcher(sampleName)))
    }
}

class DerivedApp(name: String, override val derivedFrom: BaseApp, baseDir: File, nature: AppNature) :
    App(name, baseDir, nature, derivedFrom.srcDirName), DerivedSample

class BaseLib(name: String, baseDir: File, srcDirName: String) : Sample(name, baseDir, srcDirName) {
    fun derive(suffix: String): DerivedLib {
        val sampleName = "$name-$suffix"
        return DerivedLib(sampleName, this, baseDir, srcDirName)
    }
}

class DerivedLib(name: String, override val derivedFrom: BaseLib, baseDir: File, srcDirName: String) :
    Sample(name, baseDir, srcDirName), DerivedSample

val jvmCliApp = jvmCliApp("jvm-cli-app")
val jvmUiApp = jvmUiApp("jvm-ui-app")
val jvmLib = jvmLib("jvm-lib")
val mppLib = mppLib("mpp-lib")
val nativeCliApp = nativeCliApp("native-cli-app")
val nativeUiApp = macOsUiApp("native-ui-app")

val samples = listOf(
    jvmLib,
    jvmLib.derive("customized"),

    mppLib,
    mppLib.derive("customized"),

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

val script = tasks.register("generate-script") {
    doLast {
        val scriptFile = file("run-all-2.sh")
        PrintWriter(scriptFile.bufferedWriter()).use { writer ->
            writer.run {
                for (sample in sampleApps) {
                    println(
                        """
                            if [ ! -d ${sample.distDir} ]; then
                               echo '${sample.name} distribution "${sample.distDir}" not found'
                               exit 1
                            fi
                        """.trimIndent()
                    )
                    if (sample.cliLauncher != null) {
                        println(
                            """
                            if [ ! -f ${sample.cliLauncher} ]; then
                               echo '${sample.name} launcher "${sample.cliLauncher}" not found'
                               exit 1
                            fi
                        """.trimIndent()
                        )
                    }
                    if (sample.nativeBinary != null && sample.nativeBinary != sample.cliLauncher) {
                        println(
                            """
                            if [ ! -f ${sample.nativeBinary} ]; then
                               echo '${sample.name} binary "${sample.nativeBinary}" not found'
                               exit 1
                            fi
                        """.trimIndent()
                        )
                    }
                }

                for (sample in sampleApps) {
                    println()
                    println("echo '==== ${sample.name} ===='")
                    // xargs trims whitespace, cut selects the 1st field
                    println("DU_OUT=`du -sh ${sample.distDir} | xargs | cut -f 1 -w`")
                    println("echo \"dist size: \${DU_OUT}\"")
                    if (sample.nativeBinary != null) {
                        // Select line 4 and 2nd field
                        println("OTOOL_OUT=`otool -hv ${sample.nativeBinary} | sed -n '4p' | cut -f 2 -w`")
                        println("echo \"arch: \${OTOOL_OUT}\"")
                    }
                    println("echo")
                    if (sample.cliLauncher != null) {
                        println("${sample.cliLauncher} 1 + 2")
                    } else {
                        println("echo '(UI app)'")
                    }
                    println("echo")
                }
            }
        }
        scriptFile.setExecutable(true, true)
    }
}

tasks.register("generate") {
    dependsOn(generators, script)
}

fun jvmCliApp(name: String) = BaseApp(name, projectDir, JvmCliApp(name, false), "main")

fun jvmUiApp(name: String) = BaseApp(name, projectDir, UiApp(name.capitalize()), "main")

fun nativeCliApp(name: String) = BaseApp(name, projectDir, NativeBinaryCliApp(name), "commonMain")

fun macOsUiApp(name: String) = BaseApp(name, projectDir, UiApp(name.capitalize()), "macosMain")

fun jvmLib(name: String) = BaseLib(name, projectDir, "main")

fun mppLib(name: String) = BaseLib(name, projectDir, "commonMain")
