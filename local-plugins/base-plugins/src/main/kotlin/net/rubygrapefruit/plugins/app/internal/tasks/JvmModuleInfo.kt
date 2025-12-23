package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.bytecode.BytecodeWriter
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class JvmModuleInfo : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val module: Property<String>

    @get:Input
    abstract val exports: SetProperty<String>

    @get:Input
    abstract val requires: SetProperty<String>

    @get:Input
    abstract val requiresTransitive: SetProperty<String>

    @get:Input
    abstract val generate: Property<Boolean>

    @TaskAction
    fun generate() {
        val outputDirectory = outputDirectory.get().asFile.toPath()
        outputDirectory.toFile().deleteRecursively()

        if (!generate.get()) {
            return
        }

        val requiresTransitive = requiresTransitive.get()
        val requires = requires.get() - requiresTransitive

        info("* Generating module-info.class")
        info("* module: ${module.get()}")
        info("* exports:")
        for (export in exports.get()) {
            info("  * $export")
        }
        info("* requires:")
        for (p in requires) {
            info("  * $p")
        }
        info("* requires transitive:")
        for (p in requiresTransitive) {
            info("  * $p")
        }

        val outputFile = outputDirectory.resolve("module-info.class")
        BytecodeWriter().writeTo(outputFile) {
            module(module.get(), exports.get().sorted(), requires.sorted(), requiresTransitive.sorted())
        }
    }

    private fun info(message: String) {
        logger.info(message)
    }
}