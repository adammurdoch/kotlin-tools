plugins {
    id("net.rubygrapefruit.kmp.lib")
}

val generatorTask = tasks.register<SourceGeneratorTask>("generateSource") {
    outputDir = layout.buildDirectory.dir("generated-source/jvm")
}

library {
    jvm {
        generatedSource.add(generatorTask.flatMap { it.outputDir })
    }
}

abstract class SourceGeneratorTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun exec() {
        val dir = outputDir.get().asFile
        dir.deleteRecursively()
        val sourceFile = dir.resolve("Generated.kt")
        sourceFile.parentFile.mkdirs()
        sourceFile.bufferedWriter().use { writer ->
            writer.write(
                """
                package sample.lib.kmp.generated
                
                class GeneratedKmp {
                    fun log() {
                        println("Generated KMP JVM class")
                    }
                }
            """.trimIndent()
            )
        }
    }
}