sealed class Sample(val name: String, val baseDir: File) {
    val dir = baseDir.resolve(name)

    val srcDir = dir.resolve("src/main")
}

class BaseSample(name: String, baseDir: File) : Sample(name, baseDir) {
    fun derive(suffix: String): DerivedSample = DerivedSample(suffix, this, baseDir)
}

class DerivedSample(suffix: String, val derivedFrom: BaseSample, baseDir: File) :
    Sample("${derivedFrom.name}-$suffix", baseDir)

val jvmCliApp = sample("jvm-cli-app")
val samples = listOf(
    jvmCliApp.derive("customized"),
    jvmCliApp.derive("embedded"),
    jvmCliApp.derive("embedded-customized"),
    jvmCliApp.derive("native-binary"),
    jvmCliApp.derive("native-binary-customized")
)

val generators = samples.map { sample ->
    tasks.register("generate-${sample.name}", Sync::class.java) {
        from(sample.derivedFrom.srcDir)
        into(sample.srcDir)
    }
}

tasks.register("generate") {
    dependsOn(generators)
}

fun sample(name: String): BaseSample = BaseSample(name, projectDir)
