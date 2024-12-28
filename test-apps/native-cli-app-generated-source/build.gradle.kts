plugins {
    id("net.rubygrapefruit.native.cli-app")
}

val commonGeneratorTask = tasks.register<SourceGeneratorTask>("generateCommonSource") {
    outputDir = layout.buildDirectory.dir("generated/common")
    className = "Generated"
    displayName = "common app"
}
val macosGeneratorTask = tasks.register<SourceGeneratorTask>("generateMacOSSource") {
    outputDir = layout.buildDirectory.dir("generated/macos")
    className = "GeneratedMacOS"
    displayName = "macOS app"
}

application {
    entryPoint = "sample.main"
    common {
        implementation(project(":native-lib-generated-source"))
        implementation(project(":kmp-lib-generated-source"))
    }
    generatedSource.add(commonGeneratorTask.flatMap { it.outputDir })
    macOS {
        generatedSource.add(macosGeneratorTask.flatMap { it.outputDir })
    }
}

abstract class SourceGeneratorTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val className: Property<String>

    @get:Input
    abstract val displayName: Property<String>

    @TaskAction
    fun exec() {
        val dir = outputDir.get().asFile
        dir.deleteRecursively()
        val sourceFile = dir.resolve("${className.get()}.kt")
        sourceFile.parentFile.mkdirs()
        sourceFile.bufferedWriter().use { writer ->
            writer.write(
                """
                package sample
                
                class ${className.get()} {
                    fun log() {
                        println("Generated ${displayName.get()} class")
                    }
                }
            """.trimIndent()
            )
        }
    }
}