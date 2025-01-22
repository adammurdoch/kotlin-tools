package net.rubygrapefruit.plugins.docs.internal

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.*
import org.gradle.api.tasks.OutputFile

abstract class GenerateDocs : DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:InputFiles
    abstract val sourceFiles: ConfigurableFileCollection

    @get:Input
    abstract val variables: MapProperty<String, String>

    @TaskAction
    fun generate() {
        Generator().generate(sourceFiles.map { it.toPath() }, variables.get(), outputFile.get().asFile.toPath(), outputDir.get().asFile.toPath())
    }
}