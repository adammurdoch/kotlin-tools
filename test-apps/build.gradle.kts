import java.io.PrintWriter

sealed class Sample(val name: String, val baseDir: File) {
    val dir = baseDir.resolve(name)

    val srcDir = dir.resolve("src/main")
}

sealed class App(name: String, baseDir: File, val launcher: String) : Sample(name, baseDir)

class BaseApp(name: String, baseDir: File, launcher: String) : App(name, baseDir, launcher) {
    fun derive(suffix: String, launcher: String? = null): DerivedSample {
        val sampleName = "$name-$suffix"
        return DerivedSample(sampleName, this, baseDir, launcher ?: sampleName)
    }
}

class DerivedSample(name: String, val derivedFrom: BaseApp, baseDir: File, launcher: String) :
    App(name, baseDir, launcher)

val jvmCliApp = sample("jvm-cli-app")
val samples = listOf(
    jvmCliApp,
    jvmCliApp.derive("customized", "app"),
    jvmCliApp.derive("embedded"),
    jvmCliApp.derive("embedded-customized", "app"),
    jvmCliApp.derive("native-binary"),
    jvmCliApp.derive("native-binary-customized", "app")
)

val generators = samples.filterIsInstance<DerivedSample>().map { sample ->
    tasks.register("generate-${sample.name}", Sync::class.java) {
        from(sample.derivedFrom.srcDir)
        into(sample.srcDir)
    }
}

val script = tasks.register("generate-script") {
    doLast {
        val scriptFile = file("run-all-2.sh")
        PrintWriter(scriptFile.bufferedWriter()).use { writer ->
            writer.run {
                for (sample in samples) {
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

fun sample(name: String): BaseApp = BaseApp(name, projectDir, name)
