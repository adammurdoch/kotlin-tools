package net.rubygrapefruit.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

/**
 * Creates an image of the application distribution.
 */
abstract class DistributionImage : DefaultTask() {
    @get:OutputDirectory
    abstract val imageDirectory: DirectoryProperty

    @get:InputFile
    @get:Optional
    abstract val launcherFile: RegularFileProperty

    @get:InputFiles
    abstract val libraries: ConfigurableFileCollection

    @get:Input
    abstract val launcherBaseName: Property<String>

    @TaskAction
    fun install() {
        val imageDirectory = imageDirectory.get().asFile.toPath()
        delete(imageDirectory)

        val launcherFile = launcherFile.orNull?.asFile?.toPath()
        if (launcherFile == null) {
            println("No launcher defined for this distribution")
        } else {
            println("  launcher: $launcherFile")
            Files.copy(launcherFile, imageDirectory.resolve(launcherBaseName.get()))
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

    private fun delete(imageDirectory: Path) {
        Files.walkFileTree(imageDirectory, object : FileVisitor<Path?> {
            override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                return FileVisitResult.CONTINUE
            }

            override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                if (dir != imageDirectory) {
                    Files.delete(dir)
                }
                return FileVisitResult.CONTINUE
            }
        })
    }
}