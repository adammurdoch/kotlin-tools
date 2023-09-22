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
    abstract fun derive(launcher: String): AppNature

    abstract val distDirName: String
}

class CliApp(val launcher: String, private val embeddedJvm: Boolean) : AppNature() {
    override fun derive(launcher: String): AppNature {
        return CliApp(launcher, embeddedJvm)
    }

    override val distDirName: String
        get() = "build/dist-image"
}

class UiApp(private val appName: String) : AppNature() {
    override fun derive(launcher: String): AppNature {
        return UiApp(launcher)
    }

    override val distDirName: String
        get() = "build/debug/${appName}.app"
}

sealed class App(name: String, baseDir: File, val nature: AppNature, srcDirName: String) :
    Sample(name, baseDir, srcDirName) {
    val distDir = dir.resolve(nature.distDirName)
}

class BaseApp(name: String, baseDir: File, nature: AppNature, srcDirName: String) :
    App(name, baseDir, nature, srcDirName) {

    fun derive(suffix: String, launcher: String? = null): DerivedApp {
        val sampleName = "$name-$suffix"
        return DerivedApp(sampleName, this, baseDir, nature.derive(launcher ?: sampleName))
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
    jvmCliApp.derive("customized", "app"),
    jvmCliApp.derive("embedded"),
    jvmCliApp.derive("embedded-customized", "app"),
    jvmCliApp.derive("native-binary"),
    jvmCliApp.derive("native-binary-customized", "app"),

    jvmUiApp,
    jvmUiApp.derive("customized", "App"),

    nativeCliApp,
    nativeCliApp.derive("customized", "app"),

    nativeUiApp,
    nativeUiApp.derive("customized", "App")
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
                    if (sample.nature is CliApp) {
                        println(
                            """
                            if [ ! -f ${sample.distDir}/${sample.nature.launcher} ]; then
                               echo '${sample.name} launcher "${sample.nature.launcher}" not found'
                               exit 1
                            fi
                        """.trimIndent()
                        )
                    }
                }

                for (sample in sampleApps) {
                    println()
                    println("echo '==== ${sample.name} ===='")
                    println("DU_OUT=`du -sh ${sample.distDir}`")
                    println("DU_ARR=(\$DU_OUT)")
                    println("echo \"dist size: \${DU_ARR[0]}\"")
                    println("echo")
                    if (sample.nature is CliApp) {
                        println("${sample.distDir}/${sample.nature.launcher} 1 + 2")
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

fun jvmCliApp(name: String) = BaseApp(name, projectDir, CliApp(name, false), "main")

fun jvmUiApp(name: String) = BaseApp(name, projectDir, UiApp(name.capitalize()), "main")

fun nativeCliApp(name: String) = BaseApp(name, projectDir, CliApp(name, false), "commonMain")

fun macOsUiApp(name: String) = BaseApp(name, projectDir, UiApp(name.capitalize()), "macosMain")

fun jvmLib(name: String) = BaseLib(name, projectDir, "main")

fun mppLib(name: String) = BaseLib(name, projectDir, "commonMain")
