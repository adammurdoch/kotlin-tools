package net.rubygrapefruit.plugins.convention

import net.rubygrapefruit.plugins.app.JvmLibrary
import net.rubygrapefruit.plugins.app.internal.plugins.JvmLibraryPlugin
import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
abstract class BaseJvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(JvmLibraryPlugin::class.java)
            target.plugins.apply(BuildConstants.constants.stage0.buildConstants.pluginId)

            group = BuildConstants.constants.production.libraries.group

            val library = extensions.getByType(JvmLibrary::class.java)
            library.targetJvmVersion.set(11)
        }
    }
}