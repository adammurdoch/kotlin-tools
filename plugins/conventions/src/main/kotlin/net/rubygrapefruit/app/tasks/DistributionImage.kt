package net.rubygrapefruit.app.tasks

import net.rubygrapefruit.app.internal.copyDir
import net.rubygrapefruit.app.internal.makeEmpty
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission

/**
 * Creates an image of the application distribution.
 */
abstract class DistributionImage : DefaultTask() {
    @get:OutputDirectory
    abstract val imageDirectory: DirectoryProperty

    @get:InputFile
    @get:Optional
    abstract val launcherFile: RegularFileProperty

    @get:InputDirectory
    @get:Optional
    abstract val launcherDirectory: DirectoryProperty

    @get:Input
    @get:Optional
    abstract val launcherFilePath: Property<String>

    @get:InputFiles
    abstract val libraries: ConfigurableFileCollection

    @get:Input
    abstract val launcherName: Property<String>

    @TaskAction
    fun install() {
        val imageDirectory = imageDirectory.get().asFile.toPath()
        imageDirectory.makeEmpty()

        val launcherFile = launcherFile.orNull?.asFile?.toPath()
        val launcherDir = launcherDirectory.orNull?.asFile?.toPath()
        if (launcherFile == null && launcherDir == null) {
            throw IllegalArgumentException("No launcher file or launcher directory specified.")
        } else if (launcherFile != null && launcherDir != null) {
            throw IllegalArgumentException("Both launcher file or launcher directory specified.")
        } else if (launcherFile != null) {
            println("  launcher file: $launcherFile")
            val target = imageDirectory.resolve(launcherName.get())
            Files.copy(launcherFile, target)
            Files.setPosixFilePermissions(target, setOf(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE))
        } else {
            println("  launcher dir: $launcherDir using ${launcherFilePath.get()}")
            copyDir(launcherDir!!, imageDirectory)
        }
        if (!libraries.isEmpty) {
            val libsDir = imageDirectory.resolve("libs")
            Files.createDirectories(libsDir)
            for (library in libraries) {
                println("  library: $library")
                Files.copy(library.toPath(), imageDirectory.resolve("libs/${library.name}"))
            }
        }
    }
}