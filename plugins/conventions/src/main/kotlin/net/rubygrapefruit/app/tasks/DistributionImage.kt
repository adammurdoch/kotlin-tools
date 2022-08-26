package net.rubygrapefruit.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.nio.file.Files

/**
 * Creates an image of the application distribution.
 */
abstract class DistributionImage : DefaultTask() {
    @get:OutputDirectory
    abstract val imageDirectory: DirectoryProperty

    @get:InputFile
    @get:Optional
    abstract val launcherFile: RegularFileProperty

    @get:Input
    abstract val launcherBaseName: Property<String>

    @TaskAction
    fun install() {
        val launcherFile = launcherFile.orNull
        if (launcherFile == null) {
            println("No launcher defined for this distribution")
        } else {
            println("  launcher: $launcherFile")
            Files.copy(launcherFile.asFile.toPath(), imageDirectory.get().asFile.toPath().resolve(launcherBaseName.get()))
        }
    }
}