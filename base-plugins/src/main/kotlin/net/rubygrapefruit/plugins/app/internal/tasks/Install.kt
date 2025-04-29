package net.rubygrapefruit.plugins.app.internal.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import javax.inject.Inject

abstract class Install : DefaultTask() {
    @get:Internal
    abstract val targetLauncherLink: RegularFileProperty

    @get:OutputDirectory
    abstract val targetImageDirectory: DirectoryProperty

    @get:Internal
    abstract val sourceLauncher: RegularFileProperty

    @get:InputDirectory
    abstract val sourceImageDirectory: DirectoryProperty

    @get:Inject
    protected abstract val fileOperations: FileSystemOperations

    @TaskAction
    fun install() {
        val srcDir = sourceImageDirectory.get().asFile.toPath()
        val targetDir = targetImageDirectory.get().asFile.toPath()
        fileOperations.sync { spec ->
            spec.from(srcDir)
            spec.into(targetDir)
        }

        val link = targetLauncherLink.get().asFile.toPath()
        val linkDir = link.parent
        val launcher = sourceLauncher.get().asFile.toPath()
        val launcherRelativePath = srcDir.relativize(launcher)
        val targetLauncher = linkDir.relativize(targetDir.resolve(launcherRelativePath))
        Files.deleteIfExists(link)
        Files.createSymbolicLink(link, targetLauncher)

        println("Installed into $targetDir")
        println("Run using $link")
    }
}