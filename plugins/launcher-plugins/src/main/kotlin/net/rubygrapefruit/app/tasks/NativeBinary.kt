package net.rubygrapefruit.app.tasks

import net.rubygrapefruit.download.DownloadRepository
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.net.URI

abstract class NativeBinary : DefaultTask() {
    @get:OutputFile
    abstract val launcherFile: RegularFileProperty

    @TaskAction
    fun install() {
        val repository = DownloadRepository()
        val dir = repository.install(URI("https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.2.0/graalvm-ce-java11-darwin-aarch64-22.2.0.tar.gz"), "graalvm")
    }
}