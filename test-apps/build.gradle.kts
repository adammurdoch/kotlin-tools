import java.io.PrintWriter

sealed class Sample(val name: String, val baseDir: File, val srcDirName: String = "main") {
    val dir = baseDir.resolve(name)

    val srcDir = dir.resolve("src/$srcDirName/kotlin")
}

interface DerivedSample {
    val name: String

    val derivedFrom: Sample

    val srcDir: File
}

sealed class App(name: String, baseDir: File, val launcher: String) : Sample(name, baseDir)

class BaseApp(name: String, baseDir: File, launcher: String) : App(name, baseDir, launcher) {
    fun derive(suffix: String, launcher: String? = null): DerivedApp {
        val sampleName = "$name-$suffix"
        return DerivedApp(sampleName, this, baseDir, launcher ?: sampleName)
    }
}

class DerivedApp(name: String, override val derivedFrom: BaseApp, baseDir: File, launcher: String) :
    App(name, baseDir, launcher), DerivedSample

class BaseLib(name: String, baseDir: File, srcDirName: String) : Sample(name, baseDir, srcDirName) {
    fun derive(suffix: String): DerivedLib {
        val sampleName = "$name-$suffix"
        return DerivedLib(sampleName, this, baseDir, srcDirName)
    }
}

class DerivedLib(name: String, override val derivedFrom: BaseLib, baseDir: File, srcDirName: String) :
    Sample(name, baseDir, srcDirName), DerivedSample

val jvmCliApp = app("jvm-cli-app")
val jvmUiApp = app("jvm-ui-app")
val jvmLib = lib("jvm-lib")
val mppLib = mppLib("mpp-lib")

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
    jvmUiApp.derive("customized"),
)

val generators = samples.filterIsInstance<DerivedSample>().map { sample ->
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
                for (sample in samples.filterIsInstance<App>()) {
                    println("echo '==== ${sample.name} ===='")
                    println("${sample.dir}/build/dist-image/${sample.launcher} 1 + 2")
                    println()
                }
            }
        }
        scriptFile.setExecutable(true, true)
    }
}

tasks.register("generate") {
    dependsOn(generators, script)
}

fun app(name: String) = BaseApp(name, projectDir, name)

fun lib(name: String) = BaseLib(name, projectDir, "main")

fun mppLib(name: String) = BaseLib(name, projectDir, "commonMain")
