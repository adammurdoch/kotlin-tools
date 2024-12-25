plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

val generatorTask = tasks.register<SourceGeneratorTask>("generateSource") {
    outputDir = layout.buildDirectory.dir("generated-source")
}

application {
    dependencies {
        implementation(project(":jvm-lib-generated-source"))
        implementation(project(":kmp-lib-generated-source"))
    }
    generatedSource.add(generatorTask.flatMap { it.outputDir })
}

abstract class SourceGeneratorTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun exec() {
        val dir = outputDir.get().asFile
        dir.deleteRecursively()
        val sourceFile = dir.resolve("App.kt")
        sourceFile.parentFile.mkdirs()
        sourceFile.bufferedWriter().use { writer ->
            writer.write(
                """
                package sample.app.generated
                
                fun main(args: Array<String>) {
                    sample.lib.generated.Generated()
                    sample.lib.jvm.generated.Generated()
                }
            """.trimIndent()
            )
        }
    }
}