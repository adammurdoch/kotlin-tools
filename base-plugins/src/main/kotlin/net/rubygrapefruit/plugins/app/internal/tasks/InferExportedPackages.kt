package net.rubygrapefruit.plugins.app.internal.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

abstract class InferExportedPackages : DefaultTask() {
    @get:InputFiles
    abstract val classesDirs: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun calculate() {
        outputFile.get().asFile.bufferedWriter().use {
            for (classesDir in classesDirs) {
                val seenDirs = mutableSetOf<Path>()
                Files.walkFileTree(classesDir.toPath(), object : FileVisitor<Path?> {
                    override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                        return FileVisitResult.CONTINUE
                    }

                    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                        require(file != null)
                        if (file.toFile().isFile && file.name.endsWith(".class") && seenDirs.add(file.parent)) {
                            val packageName = file.parent.relativeTo(classesDir.toPath()).pathString.replace(File.separator, ".")
                            it.write(packageName)
                            it.write("\n")
                        }
                        return FileVisitResult.CONTINUE
                    }

                    override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
                        return FileVisitResult.CONTINUE
                    }

                    override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                        return FileVisitResult.CONTINUE
                    }
                })
            }
        }
    }
}