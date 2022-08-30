package net.rubygrapefruit.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class LauncherConf : DefaultTask() {
    @get:OutputFile
    abstract val configFile: RegularFileProperty

    @get:Input
    abstract val applicationDisplayName: Property<String>

    @get:Input
    abstract val module: Property<String>

    @get:Input
    abstract val mainClass: Property<String>

    @get:Input
    abstract val javaCommand: Property<String>

    @TaskAction
    fun generate() {
        val configFile = configFile.get().asFile
        configFile.writeText(
            """
            ${applicationDisplayName.get()}
            ${javaCommand.get()}
            ${module.get()}/${mainClass.get()}
        """.trimIndent()
        )
    }
}