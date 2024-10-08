package net.rubygrapefruit.plugins.app.internal.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory

abstract class AbstractDistributionImage: DefaultTask() {
    @get:OutputDirectory
    abstract val imageDirectory: DirectoryProperty

    @get:Input
    abstract val rootDirPath: Property<String>
}