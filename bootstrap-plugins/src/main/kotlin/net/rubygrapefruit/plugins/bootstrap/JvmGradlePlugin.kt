package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import net.rubygrapefruit.plugins.app.Versions

class JvmGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("java-gradle-plugin")
            plugins.apply(JvmBasePlugin::class.java)

            dependencies.add("api", Versions.kotlin.pluginCoordinates)
            dependencies.add("implementation", Versions.plugins.bootstrapPluginCoordinates)
        }
    }
}