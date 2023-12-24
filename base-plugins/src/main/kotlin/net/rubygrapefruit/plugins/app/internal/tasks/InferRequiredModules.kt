@file:OptIn(ExperimentalSerializationApi::class)

package net.rubygrapefruit.plugins.app.internal.tasks

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
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
    abstract val outputFile: RegularFileProperty

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

        println("* requires transitive")
        val transitive = effectiveApi.values.map { moduleForFile(it) }
        println("* requires")
        val requires = effectiveRuntime.values.map { moduleForFile(it) }

        val modules = Modules(requires, transitive)
        outputFile.get().asFile.outputStream().use {
            Json.encodeToStream(modules, it)
        }
    }

    private fun moduleForFile(file: File): InferredModule {
        return JarFile(file, true, ZipFile.OPEN_READ, Runtime.version()).use { jar ->
            val moduleInfoEntry = jar.getJarEntry("module-info.class")
            if (moduleInfoEntry != null) {
                lateinit var moduleName: String
                parser.readFrom(jar.getInputStream(moduleInfoEntry), object : ClassFileVisitor {
                    override fun module(module: ModuleInfo) {
                        println("  * ${file.name} -> ${module.name}, extracted from module-info.class")
                        moduleName = module.name
                    }
                })
                InferredModule(moduleName, file.name, false)
            } else {
                val moduleName = jar.manifest.mainAttributes.getValue("Automatic-Module-Name")
                if (moduleName != null) {
                    println("  * ${file.name} -> $moduleName, extracted from manifest")
                    InferredModule(moduleName, file.name, true)
                } else {
                    var automaticName = file.name.removeSuffix(".jar")
                    val match = Regex("-(\\d+(\\.|\$))").find(automaticName)
                    if (match != null) {
                        automaticName = automaticName.substring(0, match.range.first)
                    }
                    automaticName = automaticName.replace('-', '.')
                    println("  * ${file.name} -> $automaticName, extracted from file name")
                    InferredModule(automaticName, file.name, true)
                }
            }
        }
    }
}