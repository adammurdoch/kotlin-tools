package net.rubygrapefruit.plugins.release.internal

import kotlinx.serialization.json.Json
import net.rubygrapefruit.plugins.lifecycle.Coordinates
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class ComponentCoordinates : DefaultTask() {
    @get:Input
    abstract val coordinates: Property<Coordinates>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val json = Json {
            prettyPrint = true
        }
        outputFile.get().asFile.writeText(json.encodeToString(coordinates.get()))
    }
}