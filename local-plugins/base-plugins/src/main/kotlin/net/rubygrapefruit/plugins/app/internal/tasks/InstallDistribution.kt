package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.deleteIfExists

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

        Files.walkFileTree(targetDir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                file.deleteIfExists()
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                dir.deleteIfExists()
                return FileVisitResult.CONTINUE
            }
        })

        Files.walkFileTree(srcDir, object : SimpleFileVisitor<Path>() {
            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                val targetDir = targetPath(dir)
                Files.createDirectory(targetDir)
                val permissions = Files.getPosixFilePermissions(dir)
                Files.setPosixFilePermissions(targetDir, permissions)
                return FileVisitResult.CONTINUE
            }

            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                val targetFile = targetPath(file)
                Files.copy(file, targetFile, LinkOption.NOFOLLOW_LINKS)
                val permissions = Files.getPosixFilePermissions(file)
                Files.setPosixFilePermissions(targetFile, permissions)
                return FileVisitResult.CONTINUE
            }

            private fun targetPath(path: Path) = targetDir.resolve(srcDir.relativize(path))
        })

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