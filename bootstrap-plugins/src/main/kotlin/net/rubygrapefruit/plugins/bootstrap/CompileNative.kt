package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class CompileNative : DefaultTask() {
    @get:InputFiles
    abstract val headerDirectories: ConfigurableFileCollection

    @get:InputFiles
    abstract val sourceFiles: ConfigurableFileCollection

    @get:Input
    abstract val architecture: Property<String>

    @get:OutputFile
    abstract val sharedLibrary: RegularFileProperty

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @TaskAction
    fun generate() {
        val args = listOf(
            "clang",
            "-shared",
            "-arch", architecture.get(),
            "-o",
            sharedLibrary.get().asFile.absolutePath,
            "-I${System.getProperty("java.home")}/include",
            "-I${System.getProperty("java.home")}/include/darwin"
        ) + headerDirectories.map { "-I$it" } + sourceFiles.map { it.absolutePath }
        execOperations.exec {
            it.commandLine(args)
        }
    }
}