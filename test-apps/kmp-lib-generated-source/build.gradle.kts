plugins {
    id("net.rubygrapefruit.kmp.lib")
}

val commonGeneratorTask = tasks.register<SourceGeneratorTask>("generateCommonSource") {
    outputDir = layout.buildDirectory.dir("generated/common")
    className = "GeneratedCommon"
    displayName = "KMP common lib"
}
val jvmGeneratorTask = tasks.register<SourceGeneratorTask>("generateJvmSource") {
    outputDir = layout.buildDirectory.dir("generated/jvm")
    className = "GeneratedJvm"
    displayName = "KMP JVM lib"
}
val macOSGeneratorTask = tasks.register<SourceGeneratorTask>("generateMacOSSource") {
    outputDir = layout.buildDirectory.dir("generated/macos")
    className = "GeneratedMacOS"
    displayName = "KMP macOS lib"
}

library {
    jvm {
        generatedSource.add(jvmGeneratorTask.flatMap { it.outputDir })
    }
    nativeDesktop()
    macOS {
        generatedSource.add(macOSGeneratorTask.flatMap { it.outputDir })
    }
    browser()
    generatedSource.add(commonGeneratorTask.flatMap { it.outputDir })
}

abstract class SourceGeneratorTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val displayName: Property<String>

    @get:Input
    abstract val className: Property<String>

    @TaskAction
    fun exec() {
        val dir = outputDir.get().asFile
        dir.deleteRecursively()
        val sourceFile = dir.resolve("${className.get()}.kt")
        sourceFile.parentFile.mkdirs()
        sourceFile.bufferedWriter().use { writer ->
            writer.write(
                """
                package sample.lib.kmp.generated
                
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