package net.rubygrapefruit.app.tasks

import net.rubygrapefruit.app.internal.currentOs
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.PosixFilePermission

abstract class NativeUiLauncher : DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @TaskAction
    fun extract() {
        val outputFile = outputFile.get().asFile.toPath()
        val inputFile = inputFile.get().asFile.toPath()
        Files.copy(inputFile, outputFile, StandardCopyOption.REPLACE_EXISTING)
        Files.setPosixFilePermissions(
            outputFile,
            setOf(
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_EXECUTE,
                PosixFilePermission.GROUP_READ,
                PosixFilePermission.GROUP_EXECUTE,
                PosixFilePermission.OTHERS_READ,
                PosixFilePermission.OTHERS_EXECUTE
            )
        )
    }
}