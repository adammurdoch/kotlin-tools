@file:OptIn(ExperimentalSerializationApi::class)

package net.rubygrapefruit.plugins.app.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.rubygrapefruit.plugins.app.JvmApplication
import net.rubygrapefruit.plugins.app.JvmModule
import net.rubygrapefruit.plugins.app.internal.tasks.*
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider

abstract class JvmModuleRegistry(
    private val project: Project
) {
    fun inspectClassPathsFor(
        module: JvmModule,
        jvmApplication: JvmApplication?,
        classesDirs: FileCollection,
        apiClassPath: Configuration?,
        runtimeClasspath: Configuration
    ): ModuleClasspathInspectionResults {
        val requires = project.tasks.register("requiredModules", InferModuleInfo::class.java) {
            // TODO - should not need this
            it.dependsOn(runtimeClasspath.incoming.artifacts.resolvedArtifacts.map { emptyList<String>() })
            it.runtimeLibraries.set(toLibraries(runtimeClasspath))
            it.runtimeGraph.set(runtimeClasspath.incoming.resolutionResult.rootComponent)
            if (apiClassPath != null) {
                // TODO - should not need this
                it.dependsOn(apiClassPath.incoming.artifacts.resolvedArtifacts.map { emptyList<String>() })
                it.apiLibraries.set(toLibraries(apiClassPath))
            }
            it.outputFile.set(project.layout.buildDirectory.file("jvm/module-info.txt"))
        }

        val decoded = requires.map { it.outputFile.get().asFile.inputStream().use { Json.decodeFromStream<Modules>(it) } }
        // TODO - should use convention
        module.requiresTransitive.set(decoded.map { it.transitive })
        module.requires.set(decoded.map { it.requires })

        val exports = project.tasks.register("classInfo", InspectClasses::class.java) {
            it.classesDirs.from(classesDirs)
            it.packagesFile.set(project.layout.buildDirectory.file("jvm/exported-packages.txt"))
            it.mainClassesFile.set(project.layout.buildDirectory.file("jvm/main-classes.txt"))
        }
        // TODO - should use convention
        module.exports.set(exports.map { it.packagesFile.get().asFile.readLines() })
        if (jvmApplication != null) {
            jvmApplication.mainClass.convention(exports.map { it.mainClassesFile.get().asFile.readText().trim() })
        }

        val moduleTask = project.tasks.register("moduleInfo", JvmModuleInfo::class.java) {
            it.outputDirectory.set(project.layout.buildDirectory.dir("jvm/jvm-module"))
            it.module.set(module.name)
            it.exports.set(module.exports)
            it.requires.set(module.requires)
            it.requiresTransitive.set(module.requiresTransitive)
            it.generate.set(project.provider { !project.file("src/main/java/module-info.java").isFile })
        }

        return ModuleClasspathInspectionResults(moduleTask.flatMap { it.outputDirectory }, requires.flatMap { it.outputFile })
    }

    private fun toLibraries(configuration: Configuration): Provider<List<LibraryInfo>> {
        return configuration.incoming.artifacts.resolvedArtifacts.map {
            it.map { artifact ->
                val id = artifact.id.componentIdentifier.stringId()
                LibraryInfo(id, artifact.file)
            }
        }
    }
}