package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.JvmLibrary
import net.rubygrapefruit.app.internal.JvmModuleRegistry
import net.rubygrapefruit.app.internal.checkSettingsPluginApplied
import net.rubygrapefruit.app.internal.toModuleName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

class JvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            checkSettingsPluginApplied()

            plugins.apply("java-library")
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply(JvmConventionsPlugin::class.java)

            repositories.mavenCentral()

            val lib = extensions.create("library", JvmLibrary::class.java)
            lib.module.name.convention(toModuleName(project.name))

            val moduleInfoCp = extensions.getByType(JvmModuleRegistry::class.java).moduleInfoClasspathEntryFor(lib.module)
            val sourceSet = extensions.getByType(SourceSetContainer::class.java).getByName("main")
            sourceSet.output.dir(moduleInfoCp)
        }
    }
}