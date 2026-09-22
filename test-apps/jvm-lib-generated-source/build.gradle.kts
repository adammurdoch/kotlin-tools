plugins {
    id("net.rubygrapefruit.jvm.lib")
}

val resourceGeneratorTask = tasks.register<ResourceGeneratorTask>("generateResource") {
    outputDir = layout.buildDirectory.dir("generated/resource")
}
val sourceGeneratorTask = tasks.register<SourceGeneratorTask>("generateSource") {
    outputDir = layout.buildDirectory.dir("generated/main")
}

library {
    generatedResources.add(resourceGeneratorTask.flatMap { it.outputDir })
    generatedSource.add(sourceGeneratorTask.flatMap { it.outputDir })
}

abstract class ResourceGeneratorTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun exec() {
        val dir = outputDir.get().asFile
        dir.deleteRecursively()
        val sourceFile = dir.resolve("message.txt")
        sourceFile.parentFile.mkdirs()
        sourceFile.writeText("Generated JVM lib class")
    }
}

abstract class SourceGeneratorTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun exec() {
        val dir = outputDir.get().asFile
        dir.deleteRecursively()
        val sourceFile = dir.resolve("GeneratedJvm.kt")
        sourceFile.parentFile.mkdirs()
        sourceFile.bufferedWriter().use { writer ->
            writer.write(
                """
                package sample.lib.jvm.generated
                
                class GeneratedJvm {
                    fun log(message: String) {
                        println(message)
                    }
                }
            """.trimIndent()
            )
        }
    }
}