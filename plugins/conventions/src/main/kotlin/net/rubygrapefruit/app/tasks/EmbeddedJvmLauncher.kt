package net.rubygrapefruit.app.tasks

import net.rubygrapefruit.app.internal.makeEmpty
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

    @TaskAction
    fun generate() {
        val module = module.get()
        val imageDirectory = imageDirectory.get().asFile.toPath()
        imageDirectory.makeEmpty()
        imageDirectory.deleteIfExists()

        exec.exec {
            // TODO - use jlink from toolchain
            it.commandLine(
                "jlink",
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