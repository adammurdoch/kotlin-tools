package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.checkSettingsPluginApplied
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradlePluginPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            checkSettingsPluginApplied()

            plugins.apply("java-gradle-plugin")
            plugins.apply("org.jetbrains.kotlin.jvm")

            repositories.mavenCentral()
            dependencies.add("implementation", "net.rubygrapefruit.plugins:conventions:1.0")
        }
    }
}