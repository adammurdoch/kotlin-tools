package net.rubygrapefruit.plugins.app.internal.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
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
    abstract val mainClass: Property<String>

    @get:Input
    abstract val jvmDirPath: Property<String>

    @get:Input
    abstract val startOnMainThread: Property<Boolean>

    @TaskAction
    fun generate() {
        val configFile = configFile.get().asFile
        configFile.writeText(
            """
            ${applicationDisplayName.get()}
            ${jvmDirPath.get()}
            ${mainClass.get()}
            ${startOnMainThread.get()}
        """.trimIndent()
        )
    }
}