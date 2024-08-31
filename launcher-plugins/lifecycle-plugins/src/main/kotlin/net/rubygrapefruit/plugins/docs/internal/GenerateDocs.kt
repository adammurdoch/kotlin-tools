package net.rubygrapefruit.plugins.docs.internal

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateDocs : DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:InputFiles
    abstract val sourceFiles: ConfigurableFileCollection

    @TaskAction
    fun generate() {
        Generator().generate(sourceFiles.map { it.toPath() }, outputFile.get().asFile.toPath())
    }
}