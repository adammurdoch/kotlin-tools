plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

val generatorTask = tasks.register<SourceGeneratorTask>("generateSource") {
    outputDir = layout.buildDirectory.dir("generated/main")
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