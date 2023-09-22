package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("java-gradle-plugin")
            plugins.apply(JvmBasePlugin::class.java)

            dependencies.add("api", Versions.kotlinPluginCoordinates)
            dependencies.add("implementation", Versions.bootstrapPluginCoordinates)
        }
    }
}