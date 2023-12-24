@file:OptIn(ExperimentalSerializationApi::class)

package net.rubygrapefruit.plugins.app.internal.tasks

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class RuntimeModulePath : DefaultTask() {
    @get:InputFiles
    abstract val classpath: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:InputFile
    abstract val inferredModulesFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val dir = outputDirectory.get().asFile
        dir.deleteRecursively()
        dir.mkdirs()

        val modules = inferredModulesFile.get().asFile.inputStream().use { Json.decodeFromStream<Modules>(it) }
        val modulesByFile = (modules.requires + modules.transitive).associateBy { it.fileName }

        for (file in classpath) {
            val module = modulesByFile.getValue(file.name)
            if (module.automatic) {
                println("-> NEED TO PATCH ${file.name}")
            } else {
                println("-> DO NOT PATCH ${file.name}")
            }
            file.copyTo(dir.resolve(file.name))
        }
    }
}