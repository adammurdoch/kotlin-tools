package net.rubygrapefruit.plugins.bootstrap

import net.rubygrapefruit.plugins.app.Versions
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("java-gradle-plugin")
            plugins.apply(JvmBasePlugin::class.java)

            repositories.gradlePluginPortal()

            group = Versions.plugins.group

            dependencies.add("api", Versions.kotlin.pluginCoordinates)
            dependencies.add("implementation", Versions.plugins.bootstrapPluginCoordinates)
        }
    }
}