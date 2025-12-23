package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import java.nio.file.Files
import javax.inject.Inject

abstract class InstallDistribution : DefaultTask() {
    @get:Option(option = "release", description = "Installs the release distribution")
    @get:Internal
    abstract val release: Property<Boolean>

    @get:Internal
    abstract val devDistribution: Property<Distribution.Outputs>

    @get:Internal
    abstract val releaseDistribution: Property<Distribution.Outputs>

    @get:Internal
    abstract val targetLauncherLink: RegularFileProperty

    @get:OutputDirectory
    abstract val targetImageDirectory: DirectoryProperty

    @get:Input
    val sourceLauncherPath: String
        get() {
            val outputs = if (release.get()) {
                releaseDistribution.get()
            } else {
                devDistribution.get()
            }
            return outputs.imageDirectory.get().asFile.toPath().relativize(outputs.launcherFile.get().asFile.toPath()).toString()
        }

    @get:InputDirectory
    abstract val sourceImageDirectory: DirectoryProperty

    @get:Inject
    protected abstract val fileOperations: FileSystemOperations

    init {
        release.convention(false)
        sourceImageDirectory.set(project.provider {
            if (release.get()) {
                releaseDistribution.get()
            } else {
                devDistribution.get()
            }
        }.flatMap { it.imageDirectory })
    }

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
        val launcherRelativePath = sourceLauncherPath
        val targetLauncher = linkDir.relativize(targetDir.resolve(launcherRelativePath))
        Files.deleteIfExists(link)
        Files.createSymbolicLink(link, targetLauncher)

        println("Installed into $targetDir")
        println("Run using $link")
    }
}