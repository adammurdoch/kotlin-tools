package net.rubygrapefruit.plugins.app.internal.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

abstract class Install : DefaultTask() {
    @get:Internal
    abstract val linkDir: RegularFileProperty

    @get:InputFile
    abstract val launcher: RegularFileProperty

    @TaskAction
    fun install() {
        val linkDir = linkDir.get().asFile.toPath()
        val launcher = launcher.get().asFile.toPath()
        val link = linkDir.resolve(launcher.fileName)
        Files.deleteIfExists(link)
        Files.createSymbolicLink(link, launcher)
        println("Installed at $link")
    }
}