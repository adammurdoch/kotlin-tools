package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.internal.makeEmpty
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecFactory
import javax.inject.Inject
import kotlin.io.path.deleteIfExists

abstract class EmbeddedJvmLauncher : DefaultTask() {
    @get:OutputDirectory
    abstract val imageDirectory: DirectoryProperty

    @get:Input
    abstract val module: Property<String>

    @get:InputFiles
    abstract val modulePath: ConfigurableFileCollection

    @get:Inject
    abstract val exec: ExecFactory

    @get:Input
    abstract val jlinkPath: Property<String>

    @TaskAction
    fun generate() {
        val module = module.get()
        val imageDirectory = imageDirectory.get().asFile.toPath()
        imageDirectory.makeEmpty()
        imageDirectory.deleteIfExists()

        exec.exec {
            it.commandLine(
                jlinkPath.get(),
                "--no-header-files",
                "--no-man-pages",
                "--output",
                imageDirectory.toAbsolutePath(),
                "--module-path",
                modulePath.asPath,
                "--add-modules",
                module
            )
        }
    }
}