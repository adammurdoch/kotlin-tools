plugins {
    id("net.rubygrapefruit.kmp.lib")
}

val jvmGeneratorTask = tasks.register<SourceGeneratorTask>("generateJvmSource") {
    outputDir = layout.buildDirectory.dir("generated/jvm")
    displayName = "KMP JVM lib"
}
val macOSGeneratorTask = tasks.register<SourceGeneratorTask>("generateMacOSSource") {
    outputDir = layout.buildDirectory.dir("generated/macos")
    displayName = "KMP macOS lib"
}

library {
    jvm {
        generatedSource.add(jvmGeneratorTask.flatMap { it.outputDir })
    }
    macOS {
        generatedSource.add(macOSGeneratorTask.flatMap { it.outputDir })
    }
}

abstract class SourceGeneratorTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val displayName: Property<String>

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
                        println("Generated ${displayName.get()} class")
                    }
                }
            """.trimIndent()
            )
        }
    }
}