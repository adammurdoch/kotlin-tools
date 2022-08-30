package net.rubygrapefruit.app.tasks

import net.rubygrapefruit.app.internal.copyDir
import net.rubygrapefruit.app.internal.makeEmpty
import org.gradle.api.DefaultTask
import org.gradle.api.file.*
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
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
    abstract val launcherFile: RegularFileProperty

    @get:InputFiles
    abstract val content: ConfigurableFileCollection

    @get:Input
    abstract val launcherName: Property<String>

    @get:Nested
    abstract val contributions: ListProperty<Contribution>

    @TaskAction
    fun install() {
        val imageDirectory = imageDirectory.get().asFile.toPath()
        imageDirectory.makeEmpty()

        val launcherFile = launcherFile.get().asFile.toPath()
        val target = imageDirectory.resolve(launcherName.get())
        Files.copy(launcherFile, target)
        Files.setPosixFilePermissions(target, setOf(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE))

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

                is FileContribution -> TODO()
            }
        }
        for (file in content) {
            try {
                if (file.isDirectory) {
                    copyDir(file.toPath(), imageDirectory)
                } else {
                    Files.copy(file.toPath(), imageDirectory.resolve(file.name))
                }
            } catch (e: Exception) {
                throw RuntimeException("Could not copy $file into the distribution image directory.", e)
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

    sealed class Contribution

    class FileContribution(
        @get:Input
        val filePath: String,
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