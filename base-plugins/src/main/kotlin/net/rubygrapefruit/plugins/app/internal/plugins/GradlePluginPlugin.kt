package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.checkSettingsPluginApplied
import net.rubygrapefruit.plugins.app.Versions
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradlePluginPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            checkSettingsPluginApplied()

            plugins.apply("java-gradle-plugin")
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply(JvmConventionsPlugin::class.java)

            repositories.mavenCentral()
            repositories.gradlePluginPortal()

            JvmConventionsPlugin.javaVersion(this, Versions.plugins.java)
            dependencies.add("implementation", Versions.libs.coordinates("build-constants"))
            dependencies.add("implementation", Versions.plugins.basePluginsCoordinates)
        }
    }
}