package net.rubygrapefruit.plugins.convention

import net.rubygrapefruit.plugins.app.internal.plugins.KmpLibraryPlugin
import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
abstract class BaseKmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(KmpLibraryPlugin::class.java)
            target.plugins.apply(BuildConstants.constants.stage0.buildConstants.pluginId)

            group = BuildConstants.constants.production.libraries.group
        }
    }
}