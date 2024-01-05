@file:OptIn(ExperimentalSerializationApi::class)

package net.rubygrapefruit.plugins.app.internal.tasks

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import net.rubygrapefruit.bytecode.BytecodeReader
import net.rubygrapefruit.bytecode.ClassFileVisitor
import net.rubygrapefruit.bytecode.ModuleInfo
import net.rubygrapefruit.plugins.app.internal.stringId
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.util.jar.JarFile
import java.util.zip.ZipFile

abstract class InferModuleInfo : DefaultTask() {

    @get:Nested
    abstract val apiLibraries: ListProperty<LibraryInfo>

    @get:Input
    abstract val runtimeGraph: Property<ResolvedComponentResult>

    @get:Nested
    abstract val runtimeLibraries: ListProperty<LibraryInfo>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun calculate() {
        val apiElements = apiLibraries.get().associateBy { it.componentId }
        val runtimeElements = runtimeLibraries.get().associateBy { it.componentId }

        val effectiveApi = mutableMapOf<String, LibraryInfo>()
        for (entry in apiElements) {
            val runtimeEntry = runtimeElements[entry.key]
            if (runtimeEntry != null) {
                effectiveApi[entry.key] = runtimeEntry
            } else {
                effectiveApi[entry.key] = entry.value
            }
        }

        val effectiveRuntime = mutableMapOf<String, LibraryInfo>()
        for (entry in runtimeElements) {
            if (!effectiveApi.containsKey(entry.key)) {
                effectiveRuntime[entry.key] = entry.value
            }
        }

        println("* effective API elements:")
        for (entry in effectiveApi.entries) {
            println("  * ${entry.key} -> ${entry.value.file.name}")
        }
        println("* effective runtime elements:")
        for (entry in effectiveRuntime.entries) {
            println("  * ${entry.key} -> ${entry.value.file.name}")
        }

        println("* requires transitive")
        val index = ModuleIndex(runtimeGraph.get(), apiLibraries.get(), runtimeLibraries.get())
        val transitive = effectiveApi.values.map { index.moduleFor(it) }
        println("* requires")
        val requires = effectiveRuntime.values.map { index.moduleFor(it) }

        val modules = Modules(requires + transitive, requires.map { it.name }, transitive.map { it.name })
        outputFile.get().asFile.outputStream().use {
            Json.encodeToStream(modules, it)
        }
    }

    private class ModuleIndex(rootComponent: ResolvedComponentResult, apiLibraries: List<LibraryInfo>, runtimeLibraries: List<LibraryInfo>) {
        private val parser = BytecodeReader()
        private val componentDependencies: Map<String, Set<LibraryInfo>>
        private val moduleForLibrary = mutableMapOf<String, InferredModule>()

        init {
            val libForComponentId = apiLibraries.associateBy { it.componentId } + runtimeLibraries.associateBy { it.componentId }
            val seen = mutableSetOf<String>()
            val queue = mutableListOf(rootComponent)
            val result = mutableMapOf<String, Set<LibraryInfo>>()
            while (queue.isNotEmpty()) {
                val component = queue.first()
                val id = component.id.stringId()

                if (result.containsKey(id)) {
                    // Already visited
                    queue.removeFirst()
                    continue
                }

                val dependencies = component.dependencies.filterIsInstance<ResolvedDependencyResult>().filter { !it.isConstraint }.map { it.selected }

                if (seen.add(id)) {
                    // Not seen yet, visit dependencies
                    queue.addAll(0, dependencies)
                    continue
                }
                queue.removeFirst()

                val requires = dependencies.flatMap { dep ->
                    // TODO - if component has no artifact, inline its dependencies here
                    val depId = dep.id.stringId()
                    val depLib = libForComponentId[depId]
                    if (depLib != null) {
                        // Has an artifact, require
                        listOf(depLib)
                    } else {
                        // No artifact, so require its dependencies
                        result.getValue(depId)
                    }
                }.toSet()
                result[id] = requires
                queue.addAll(dependencies)
            }

            componentDependencies = result
        }

        fun moduleFor(library: LibraryInfo): InferredModule {
            val module = moduleForLibrary.get(library.componentId)
            if (module != null) {
                return module
            }
            val file = library.file
            val newModule = JarFile(file, true, ZipFile.OPEN_READ, Runtime.version()).use { jar ->
                val moduleInfoEntry = jar.getJarEntry("module-info.class")
                if (moduleInfoEntry != null) {
                    lateinit var moduleName: String
                    parser.readFrom(jar.getInputStream(moduleInfoEntry), object : ClassFileVisitor {
                        override fun module(module: ModuleInfo) {
                            println("  * ${file.name} -> ${module.name}, extracted from module-info.class")
                            moduleName = module.name
                        }
                    })
                    InferredModule(moduleName, file.name, false, emptyList())
                } else {
                    val dependencies = componentDependencies[library.componentId] ?: emptySet()
                    val requires = dependencies.map { moduleFor(it).name } + listOf("java.logging", "java.management")

                    val moduleName = jar.manifest.mainAttributes.getValue("Automatic-Module-Name")
                    if (moduleName != null) {
                        println("  * ${file.name} -> $moduleName, extracted from manifest")
                        InferredModule(moduleName, file.name, true, requires)
                    } else {
                        var automaticName = file.name.removeSuffix(".jar")
                        val match = Regex("-(\\d+(\\.|\$))").find(automaticName)
                        if (match != null) {
                            automaticName = automaticName.substring(0, match.range.first)
                        }
                        automaticName = automaticName.replace('-', '.')
                        println("  * ${file.name} -> $automaticName, extracted from file name")
                        InferredModule(automaticName, file.name, true, requires)
                    }
                }
            }
            moduleForLibrary.put(library.componentId, newModule)
            return newModule
        }
    }
}