package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmModule
import net.rubygrapefruit.app.tasks.JvmModuleInfo
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

abstract class JvmModuleRegistry(
    private val project: Project
) {
    fun moduleInfoClasspathEntryFor(module: JvmModule): Provider<Directory> {
        val moduleTask = project.tasks.register("moduleInfo", JvmModuleInfo::class.java) {
            it.outputDirectory.set(project.layout.buildDirectory.dir("app/jvm-module"))
            it.module.set(module.name)
            it.exports.set(module.exports)
            it.requires.set(module.requires)
            it.requiresTransitive.set(module.requiresTransitive)
            it.generate.set(project.provider { !project.file("src/main/java/module-info.java").isFile })
        }
        return moduleTask.flatMap { it.outputDirectory }
    }
}