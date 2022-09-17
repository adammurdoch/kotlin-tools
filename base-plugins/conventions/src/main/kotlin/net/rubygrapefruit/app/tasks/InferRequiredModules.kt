package net.rubygrapefruit.app.tasks

import net.rubygrapefruit.bytecode.BytecodeReader
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.util.jar.JarFile
import java.util.zip.ZipFile

abstract class InferRequiredModules : DefaultTask() {
    @get:InputFiles
    abstract val classPath: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun calculate() {
        outputFile.get().asFile.bufferedWriter().use { writer ->
            val parser = BytecodeReader()
            for (file in classPath) {
                JarFile(file, true, ZipFile.OPEN_READ, Runtime.version()).use { jar ->
                    val moduleInfoEntry = jar.getJarEntry("module-info.class")
                    if (moduleInfoEntry != null) {
                        parser.readFrom(jar.getInputStream(moduleInfoEntry), object : BytecodeReader.Visitor {
                            override fun module(name: String) {
                                writer.write(name)
                                writer.write("\n")
                            }
                        })
                    } else {
                        val moduleName = jar.manifest.mainAttributes.getValue("Automatic-Module-Name")
                        if (moduleName != null) {
                            writer.write(moduleName)
                            writer.write("\n")
                        }
                    }
                }
            }
        }
    }
}