package net.rubygrapefruit.plugins.samples.internal

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateSamples : DefaultTask() {
    @get:InputFiles
    abstract val sourceDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val outputDirectory = outputDirectory.get().asFile
        outputDirectory.deleteRecursively()

        for (file in sourceDirectory.get().asFile.listFiles()) {
            if (file.isDirectory) {
                println("Generating sample '${file.name}'")
                val sampleDestDir = outputDirectory.resolve(file.name)
                sampleDestDir.mkdirs()
                sampleDestDir.resolve("settings.gradle.kts").writeText("")

                file.copyRecursively(sampleDestDir)
            }
        }
    }
}