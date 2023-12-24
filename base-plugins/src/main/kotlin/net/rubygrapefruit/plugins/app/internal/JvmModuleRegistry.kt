package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmApplication
import net.rubygrapefruit.plugins.app.JvmModule
import net.rubygrapefruit.plugins.app.internal.tasks.InferRequiredModules
import net.rubygrapefruit.plugins.app.internal.tasks.InspectClasses
import net.rubygrapefruit.plugins.app.internal.tasks.JvmModuleInfo
import net.rubygrapefruit.plugins.app.internal.tasks.LibraryInfo
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
            it.requiresTransitiveOutputFile.set(project.layout.buildDirectory.file("jvm/requires-transitive-modules.txt"))
            it.requiresOutputFile.set(project.layout.buildDirectory.file("jvm/requires-modules.txt"))
        }
        // TODO - should use convention
        module.requiresTransitive.set(requires.map { it.requiresTransitiveOutputFile.get().asFile.readLines() })
        module.requires.set(requires.map { it.requiresOutputFile.get().asFile.readLines() })

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