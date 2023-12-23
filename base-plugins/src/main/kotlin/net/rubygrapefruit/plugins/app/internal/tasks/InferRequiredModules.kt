package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.bytecode.BytecodeReader
import net.rubygrapefruit.bytecode.ClassFileVisitor
import net.rubygrapefruit.bytecode.ModuleInfo
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
                        parser.readFrom(jar.getInputStream(moduleInfoEntry), object : ClassFileVisitor {
                            override fun module(module: ModuleInfo) {
                                println("* using ${module.name} for ${file.name}, extracted from module-info.class")
                                writer.write(module.name)
                                writer.write("\n")
                            }
                        })
                    } else {
                        val moduleName = jar.manifest.mainAttributes.getValue("Automatic-Module-Name")
                        if (moduleName != null) {
                            println("* using $moduleName for ${file.name}, extracted from manifest")
                            writer.write(moduleName)
                            writer.write("\n")
                        } else {
                            var automaticName = file.name.removeSuffix(".jar")
                            val match = Regex("-(\\d+(\\.|\$))").find(automaticName)
                            if (match != null) {
                               automaticName = automaticName.substring(0, match.range.first)
                            }
                            automaticName = automaticName.replace('-', '.')
                            println("* using $automaticName for ${file.name}, extracted from file name")
                            writer.write(automaticName)
                            writer.write("\n")
                        }
                    }
                }
            }
        }
    }
}