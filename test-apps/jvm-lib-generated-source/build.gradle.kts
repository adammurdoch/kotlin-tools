plugins {
    id("net.rubygrapefruit.jvm.lib")
}

val generatorTask = tasks.register<SourceGeneratorTask>("generateSource") {
    outputDir = layout.buildDirectory.dir("generated-source")
}

library {
    kotlin.add(generatorTask.flatMap { it.outputDir })
}

abstract class SourceGeneratorTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun exec() {
        val dir = outputDir.get().asFile
        val sourceFile = dir.resolve("Generated.kt")
        sourceFile.parentFile.mkdirs()
        sourceFile.bufferedWriter().use { writer ->
            writer.write("""
                package sample.lib.generated
                
                class Generated
            """.trimIndent())
        }
    }
}