package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmApplication
import net.rubygrapefruit.plugins.app.JvmModule
import net.rubygrapefruit.plugins.app.internal.tasks.InspectClasses
import net.rubygrapefruit.plugins.app.internal.tasks.InferRequiredModules
import net.rubygrapefruit.plugins.app.internal.tasks.JvmModuleInfo
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

abstract class JvmModuleRegistry(
    private val project: Project
) {
    fun inspectClassPathsFor(
        module: JvmModule,
        jvmApplication: JvmApplication?,
        classesDirs: FileCollection,
        apiClassPath: FileCollection?,
        compileClasspath: FileCollection
    ): ModuleClasspathInspectionResults {
        if (apiClassPath != null) {
            val requiresTransitive =
                project.tasks.register("requiredTransitiveModules", InferRequiredModules::class.java) {
                    it.classPath.from(apiClassPath)
                    it.outputFile.set(project.layout.buildDirectory.file("jvm/required-transitive-modules.txt"))
                }
            module.requiresTransitive.convention(requiresTransitive.map { it.outputFile.get().asFile.readLines() })
        }

        val requires = project.tasks.register("requiredModules", InferRequiredModules::class.java) {
            it.classPath.from(compileClasspath)
            it.outputFile.set(project.layout.buildDirectory.file("jvm/required-modules.txt"))
        }
        module.requires.convention(requires.map { it.outputFile.get().asFile.readLines() })

        val exports = project.tasks.register("classInfo", InspectClasses::class.java) {
            it.classesDirs.from(classesDirs)
            it.packagesFile.set(project.layout.buildDirectory.file("jvm/exported-packages.txt"))
            it.mainClassesFile.set(project.layout.buildDirectory.file("jvm/main-classes.txt"))
        }
        module.exports.convention(exports.map { it.packagesFile.get().asFile.readLines() })
        if (jvmApplication != null) {
            jvmApplication.mainClass.convention(exports.map { it.mainClassesFile.get().asFile.readLines().first() })
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
}