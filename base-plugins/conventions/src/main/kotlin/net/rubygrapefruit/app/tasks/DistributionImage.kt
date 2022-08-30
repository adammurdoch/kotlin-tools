package net.rubygrapefruit.app.tasks

import net.rubygrapefruit.app.internal.copyDir
import net.rubygrapefruit.app.internal.makeEmpty
import org.gradle.api.DefaultTask
import org.gradle.api.file.*
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Creates an image of the application distribution.
 */
abstract class DistributionImage : DefaultTask() {
    @get:OutputDirectory
    abstract val imageDirectory: DirectoryProperty

    @get:Nested
    abstract val contributions: ListProperty<Contribution>

    @TaskAction
    fun install() {
        val imageDirectory = imageDirectory.get().asFile.toPath()
        imageDirectory.makeEmpty()

        for (contribution in contributions.get()) {
            when (contribution) {
                is FilesContribution -> {
                    val sourceFiles = contribution.files.get()
                    if (sourceFiles.isNotEmpty()) {
                        val targetDir = imageDirectory.resolve(contribution.dirPath)
                        Files.createDirectories(targetDir)
                        for (sourceFile in sourceFiles) {
                            val file = sourceFile.asFile
                            if (file.isDirectory) {
                                copyDir(file.toPath(), targetDir.resolve(file.name))
                            } else {
                                Files.copy(file.toPath(), targetDir.resolve(file.name))
                            }
                        }
                    }
                }

                is DirectoryContribution -> {
                    val sourceDir = contribution.dir.orNull
                    if (sourceDir != null) {
                        val targetDir = imageDirectory.resolve(contribution.dirPath)
                        Files.createDirectories(targetDir)
                        copyDir(sourceDir.asFile.toPath(), targetDir)
                    }
                }

                is FileContribution -> {
                    val sourceFile = contribution.file.orNull
                    if (sourceFile != null) {
                        val targetFile = imageDirectory.resolve(contribution.filePath.get())
                        Files.createDirectories(targetFile.parent)
                        Files.copy(sourceFile.asFile.toPath(), targetFile, StandardCopyOption.COPY_ATTRIBUTES)
                    }
                }
            }
        }
    }

    /**
     * Includes the given files and directories in the given directory in the image.
     */
    fun includeFilesInDir(dirPath: String, files: FileCollection) {
        contributions.add(FilesContribution(dirPath, files.elements))
    }

    /**
     * Includes the given directory in the image. The dir provider can be undefined.
     */
    fun includeDir(dirPath: String, dir: Provider<Directory>) {
        contributions.add(DirectoryContribution(dirPath, dir))
    }

    /**
     * Includes the given file in the image. The file provider can be undefined.
     */
    fun includeFile(filePath: String, file: Provider<RegularFile>) {
        contributions.add(FileContribution(project.provider { filePath }, file))
    }

    /**
     * Includes the given file in the image. The file provider can be undefined.
     */
    fun includeFile(filePath: Provider<String>, file: Provider<RegularFile>) {
        contributions.add(FileContribution(filePath, file))
    }

    sealed class Contribution

    class FileContribution(
        @get:Input
        val filePath: Provider<String>,
        @get:InputFile
        val file: Provider<RegularFile>
    ) : Contribution()

    class DirectoryContribution(
        @get:Input
        val dirPath: String,
        @get:InputDirectory
        val dir: Provider<Directory>
    ) : Contribution()

    class FilesContribution(
        @get:Input
        val dirPath: String,
        @get:InputFiles
        val files: Provider<Set<FileSystemLocation>>
    ) : Contribution()
}