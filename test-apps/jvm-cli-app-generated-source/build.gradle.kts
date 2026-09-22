plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

val resourceGeneratorTask = tasks.register<ResourceGeneratorTask>("generateResource") {
    outputDir = layout.buildDirectory.dir("generated/resource")
}
val sourceGeneratorTask = tasks.register<SourceGeneratorTask>("generateSource") {
    outputDir = layout.buildDirectory.dir("generated/main")
}

application {
    dependencies {
        implementation(project(":jvm-lib-generated-source"))
        implementation(project(":kmp-lib-generated-source"))
    }
    generatedSource.add(sourceGeneratorTask.flatMap { it.outputDir })
    generatedResources.add(resourceGeneratorTask.flatMap { it.outputDir })
}

abstract class ResourceGeneratorTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun exec() {
        val dir = outputDir.get().asFile
        dir.deleteRecursively()
        val sourceFile = dir.resolve("jvm-cli-app-message.txt")
        sourceFile.parentFile.mkdirs()
        sourceFile.writeText("Generated JVM app resource")
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
                package sample
                
                class Generated {
                    fun log() {
                        println("Generated JVM app class")
                    }
                }
            """.trimIndent()
            )
        }
    }
}