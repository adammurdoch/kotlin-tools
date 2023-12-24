package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.bytecode.BytecodeReader
import net.rubygrapefruit.bytecode.ClassFileVisitor
import net.rubygrapefruit.bytecode.ModuleInfo
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.jar.JarFile
import java.util.zip.ZipFile

abstract class InferRequiredModules : DefaultTask() {
    @get:Nested
    abstract val apiClassPath: ListProperty<LibraryInfo>

    @get:Nested
    abstract val runtimeClassPath: ListProperty<LibraryInfo>

    @get:OutputFile
    abstract val requiresOutputFile: RegularFileProperty

    @get:OutputFile
    abstract val requiresTransitiveOutputFile: RegularFileProperty

    private val parser = BytecodeReader()

    @TaskAction
    fun calculate() {
        val apiElements = apiClassPath.get().associateBy { it.module }
        val runtimeElements = runtimeClassPath.get().associateBy { it.module }

        val effectiveApi = mutableMapOf<String, File>()
        for (entry in apiElements) {
            val runtimeEntry = runtimeElements[entry.key]
            if (runtimeEntry != null) {
                effectiveApi[entry.key] = runtimeEntry.file
            } else {
                effectiveApi[entry.key] = entry.value.file
            }
        }

        val effectiveRuntime = mutableMapOf<String, File>()
        for (entry in runtimeElements) {
            if (!effectiveApi.containsKey(entry.key)) {
                effectiveRuntime[entry.key] = entry.value.file
            }
        }

        println("* effective API elements:")
        for (entry in effectiveApi.entries) {
            println("  * ${entry.key} -> ${entry.value.name}")
        }
        println("* effective runtime elements:")
        for (entry in effectiveRuntime.entries) {
            println("  * ${entry.key} -> ${entry.value.name}")
        }

        requiresTransitiveOutputFile.get().asFile.bufferedWriter().use { writer ->
            for (lib in effectiveApi.values) {
                writer.write(moduleForFile(lib))
                writer.write("\n")
            }
        }
        requiresOutputFile.get().asFile.bufferedWriter().use { writer ->
            for (lib in effectiveRuntime.values) {
                writer.write(moduleForFile(lib))
                writer.write("\n")
            }
        }
    }

    private fun moduleForFile(file: File): String {
        return JarFile(file, true, ZipFile.OPEN_READ, Runtime.version()).use { jar ->
            val moduleInfoEntry = jar.getJarEntry("module-info.class")
            if (moduleInfoEntry != null) {
                lateinit var moduleName: String
                parser.readFrom(jar.getInputStream(moduleInfoEntry), object : ClassFileVisitor {
                    override fun module(module: ModuleInfo) {
                        println("* using '${module.name}' for ${file.name}, extracted from module-info.class")
                        moduleName = module.name
                    }
                })
                moduleName
            } else {
                val moduleName = jar.manifest.mainAttributes.getValue("Automatic-Module-Name")
                if (moduleName != null) {
                    println("* using '$moduleName' for ${file.name}, extracted from manifest")
                    moduleName
                } else {
                    var automaticName = file.name.removeSuffix(".jar")
                    val match = Regex("-(\\d+(\\.|\$))").find(automaticName)
                    if (match != null) {
                        automaticName = automaticName.substring(0, match.range.first)
                    }
                    automaticName = automaticName.replace('-', '.')
                    println("* using '$automaticName' for ${file.name}, extracted from file name")
                    automaticName
                }
            }
        }
    }
}