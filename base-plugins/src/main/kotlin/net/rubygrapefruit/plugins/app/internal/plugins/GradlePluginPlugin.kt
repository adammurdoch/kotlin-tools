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

            JvmConventionsPlugin.javaVersion(this, Versions.pluginsJava)
            dependencies.add("implementation", "${Versions.pluginsGroup}:base-plugins:${Versions.pluginsVersion}")
        }
    }
}