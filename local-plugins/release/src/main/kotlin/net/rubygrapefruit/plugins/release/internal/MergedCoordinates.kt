package net.rubygrapefruit.plugins.release.internal

import kotlinx.serialization.json.Json
import net.rubygrapefruit.plugins.lifecycle.Coordinates
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class MergedCoordinates : DefaultTask() {
    @get:InputFiles
    abstract val inputFiles: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val coordinates = inputFiles.files.map { file ->
            Json.decodeFromString<Coordinates>(file.readText())
        }
        println("Coordinates: $coordinates")
        outputFile.get().asFile.writeText(Json.encodeToString(coordinates))
    }
}