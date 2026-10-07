package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.bytecode.BytecodeReader
import net.rubygrapefruit.bytecode.ClassFileVisitor
import net.rubygrapefruit.bytecode.TypeInfo
import net.rubygrapefruit.bytecode.TypeVisitor
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.BufferedWriter
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.inputStream
import kotlin.io.path.name

abstract class InspectClasses : DefaultTask() {
    @get:InputFiles
    abstract val classesDirs: ConfigurableFileCollection

    @get:OutputFile
    abstract val packagesFile: RegularFileProperty

    @TaskAction
    fun calculate() {
        packagesFile.get().asFile.bufferedWriter().use { exportedPackages ->
            visitFiles(exportedPackages)
        }
    }

    private fun visitFiles(exportedPackages: BufferedWriter) {
        val seenPackages = mutableSetOf<String>()
        for (classesDir in classesDirs) {
            Files.walkFileTree(classesDir.toPath(), object : FileVisitor<Path> {
                override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                    return FileVisitResult.CONTINUE
                }

                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    if (file.toFile().isFile && file.name.endsWith(".class")) {
                        visitClassFile(file, seenPackages, exportedPackages)
                    }
                    return FileVisitResult.CONTINUE
                }

                override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
                    return FileVisitResult.CONTINUE
                }

                override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                    return FileVisitResult.CONTINUE
                }
            })
        }
    }

    private fun visitClassFile(
        file: Path,
        seenPackages: MutableSet<String>,
        exportedPackages: BufferedWriter
    ) {
        file.inputStream().use { stream ->
            BytecodeReader().readFrom(stream, object : ClassFileVisitor {
                override fun type(type: TypeInfo): TypeVisitor? {
                    val packageName = type.name.substringBeforeLast(".", "")
                    if (packageName.isNotEmpty() && seenPackages.add(packageName)) {
                        exportedPackages.write(packageName)
                        exportedPackages.write("\n")
                    }
                    return null
                }
            })
        }
    }
}