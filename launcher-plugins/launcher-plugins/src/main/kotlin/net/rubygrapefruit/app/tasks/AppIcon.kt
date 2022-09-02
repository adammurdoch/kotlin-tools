package net.rubygrapefruit.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.nio.file.Files
import javax.inject.Inject
import kotlin.io.path.pathString

abstract class AppIcon : DefaultTask() {
    @get:OutputDirectory
    abstract val outputIconSet: DirectoryProperty

    @get:OutputFile
    abstract val outputIcon: RegularFileProperty

    @get:InputFile
    abstract val sourceIcon: RegularFileProperty

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun generate() {
        val sourceIcon = sourceIcon.get().asFile.toPath()
        val iconSetDir = outputIconSet.get().asFile.toPath()
        val iconFile = outputIcon.get().asFile.toPath()

        Files.createDirectories(iconSetDir)
        for (size in listOf(16, 32, 128, 256, 512)) {
            execOperations.exec {
                it.commandLine("sips", "-z", size.toString(), size.toString(), sourceIcon.pathString, "--out", "$iconSetDir/icon_${size}x${size}.png")
            }
            execOperations.exec {
                it.commandLine("sips", "-z", (size * 2).toString(), (size * 2).toString(), sourceIcon.pathString, "--out", "$iconSetDir/icon_${size}x${size}@2x.png")
            }
        }
        execOperations.exec {
            it.commandLine("iconutil", "-c", "icns", "-o", iconFile.pathString, iconSetDir.pathString)
        }
    }
}