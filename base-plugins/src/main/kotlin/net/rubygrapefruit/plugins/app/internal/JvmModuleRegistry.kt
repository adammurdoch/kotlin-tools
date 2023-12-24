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
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
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
        compileClasspath: Configuration
    ): ModuleClasspathInspectionResults {
        val requires = project.tasks.register("requiredModules", InferRequiredModules::class.java) {
            // TODO - should not need this
            it.dependsOn(compileClasspath.incoming.artifacts.resolvedArtifacts.map { emptyList<String>() })
            it.runtimeClassPath.set(toLibraries(compileClasspath))
            if (apiClassPath != null) {
                // TODO - should not need this
                it.dependsOn(apiClassPath.incoming.artifacts.resolvedArtifacts.map { emptyList<String>() })
                it.apiClassPath.set(toLibraries(apiClassPath))
            }
            it.outputFile.set(project.layout.buildDirectory.file("jvm/module-info.txt"))
        }

        val decoded = requires.map { it.outputFile.get().asFile.inputStream().use { Json.decodeFromStream<Modules>(it) } }
        // TODO - should use convention
        module.requiresTransitive.set(decoded.map { it.transitive.map { it.name } })
        module.requires.set(decoded.map { it.requires.map { it.name } })

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

        return ModuleClasspathInspectionResults(moduleTask.flatMap { it.outputDirectory })
    }

    private fun toLibraries(configuration: Configuration): Provider<List<LibraryInfo>> {
        return configuration.incoming.artifacts.resolvedArtifacts.map {
            it.map {
                val componentIdentifier = it.id.componentIdentifier
                val id = when (componentIdentifier) {
                    is ModuleComponentIdentifier -> "module:${componentIdentifier.group}:${componentIdentifier.module}"
                    is ProjectComponentIdentifier -> "project:${componentIdentifier.buildTreePath}"
                    else -> throw UnsupportedOperationException()
                }
                LibraryInfo(id, it.file)
            }
        }
    }
}