package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

internal abstract class UpdateVersion : DefaultTask() {
    @get:Input
    abstract val version: Property<VersionNumber>

    @get:Internal
    abstract val buildFile: RegularFileProperty

    @TaskAction
    fun update() {
        println("Next version: ${version.get()}")
    }
}