package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmModule
import net.rubygrapefruit.plugins.app.tasks.InferExportedPackages
import net.rubygrapefruit.plugins.app.tasks.InferRequiredModules
import net.rubygrapefruit.plugins.app.tasks.JvmModuleInfo
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider

abstract class JvmModuleRegistry(
    private val project: Project
) {
    fun moduleInfoClasspathEntryFor(
        module: JvmModule,
        classesDirs: FileCollection?,
        apiClassPath: FileCollection?,
        compileClasspath: FileCollection
    ): Provider<Directory> {
        if (apiClassPath != null) {
            val requiresTransitive =
                project.tasks.register("requiredTransitiveModules", InferRequiredModules::class.java) {
                    it.classPath.from(apiClassPath)
                    it.outputFile.set(project.layout.buildDirectory.file("jvm/required-transitive-modules.txt"))
                }
            module.requiresTransitive.set(requiresTransitive.map { it.outputFile.get().asFile.readLines() })
        }

        val requires = project.tasks.register("requiredModules", InferRequiredModules::class.java) {
            it.classPath.from(compileClasspath)
            it.outputFile.set(project.layout.buildDirectory.file("jvm/required-modules.txt"))
        }
        module.requires.set(requires.map { it.outputFile.get().asFile.readLines() })

        if (classesDirs != null) {
            val exports = project.tasks.register("exportedPackages", InferExportedPackages::class.java) {
                it.classesDirs.from(classesDirs)
                it.outputFile.set(project.layout.buildDirectory.file("jvm/exported-packages.txt"))
            }
            module.exports.set(exports.map { it.outputFile.get().asFile.readLines() })
        }

        val moduleTask = project.tasks.register("moduleInfo", JvmModuleInfo::class.java) {
            it.outputDirectory.set(project.layout.buildDirectory.dir("jvm/jvm-module"))
            it.module.set(module.name)
            it.exports.set(module.exports)
            it.requires.set(module.requires)
            it.requiresTransitive.set(module.requiresTransitive)
            it.generate.set(project.provider { !project.file("src/main/java/module-info.java").isFile })
        }
        return moduleTask.flatMap { it.outputDirectory }
    }
}