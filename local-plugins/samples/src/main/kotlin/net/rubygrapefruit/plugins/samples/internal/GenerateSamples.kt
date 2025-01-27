package net.rubygrapefruit.plugins.samples.internal

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateSamples : DefaultTask() {
    @get:InputFiles
    abstract val sourceDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:OutputFile
    abstract val manifest: RegularFileProperty

    @TaskAction
    fun generate() {
        val outputDirectory = outputDirectory.get().asFile
        outputDirectory.deleteRecursively()

        val samples = mutableListOf<File>()

        for (file in sourceDirectory.get().asFile.listFiles()) {
            if (file.isDirectory) {
                println("Generating sample '${file.name}'")
                val sampleDestDir = outputDirectory.resolve(file.name)
                sampleDestDir.mkdirs()
                sampleDestDir.resolve("settings.gradle.kts").writeText("")

                file.copyRecursively(sampleDestDir)
                samples.add(sampleDestDir)
            }
        }

        manifest.get().asFile.writeText(samples.joinToString("\n"))
    }
}