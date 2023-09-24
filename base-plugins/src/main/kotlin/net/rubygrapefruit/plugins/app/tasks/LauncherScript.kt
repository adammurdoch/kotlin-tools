package net.rubygrapefruit.plugins.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile

abstract class LauncherScript : DefaultTask() {
    @get:OutputFile
    abstract val scriptFile: RegularFileProperty

    @get:Input
    abstract val module: Property<String>

    @get:Input
    abstract val mainClass: Property<String>

    @get:Input
    abstract val modulePath: ListProperty<String>

    @get:Input
    abstract val libsDirPath: Property<String>

    @get:Input
    @get:Optional
    abstract val javaLauncherPath: Property<String>
}