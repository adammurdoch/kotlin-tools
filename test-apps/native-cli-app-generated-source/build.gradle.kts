plugins {
    id("net.rubygrapefruit.native.cli-app")
}

val generatorTask = tasks.register<SourceGeneratorTask>("generateSource") {
    outputDir = layout.buildDirectory.dir("generated-source")
}

application {
    entryPoint = "sample.main"
    common {
        implementation(project(":native-lib-generated-source"))
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
        val sourceFile = dir.resolve("Generated.kt")
        sourceFile.parentFile.mkdirs()
        sourceFile.bufferedWriter().use { writer ->
            writer.write(
                """
                package sample
                
                class Generated {
                    fun log() {
                        println("Generated app class")
                    }
                }
            """.trimIndent()
            )
        }
    }
}