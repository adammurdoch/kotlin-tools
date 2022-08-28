package net.rubygrapefruit.app.tasks

import net.rubygrapefruit.app.internal.makeEmpty
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecFactory
import javax.inject.Inject
import kotlin.io.path.deleteIfExists

abstract class EmbeddedJvmLauncher : DefaultTask() {
    @get:OutputDirectory
    abstract val imageDirectory: DirectoryProperty

    @get:InputFiles
    abstract val modulePath: ConfigurableFileCollection

    @get:Inject
    abstract val exec: ExecFactory

    @TaskAction
    fun generate() {
        val module = "someModule"
        val launcherName = "thing"
        val mainClasName = "sample.MainKt"
        val imageDirectory = imageDirectory.get().asFile.toPath()
        imageDirectory.makeEmpty()
        imageDirectory.deleteIfExists()

        exec.exec {
            // TODO - use jlink from toolchain
            // TODO - configurable module name
            // TODO - pass in launcher name
            // TODO - pass in main class
            // TODO - strip the image
            it.commandLine(
                "jlink",
                "--verbose",
                "--output",
                imageDirectory.toAbsolutePath(),
                "--module-path",
                modulePath.asPath,
                "--add-modules",
                module,
                "--launcher",
                "$launcherName=$module/$mainClasName"
            )
        }
    }
}