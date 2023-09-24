package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.checkSettingsPluginApplied
import net.rubygrapefruit.plugins.bootstrap.Versions
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

            extensions.create("versions", net.rubygrapefruit.app.Versions::class.java)

            JvmConventionsPlugin.javaVersion(this, Versions.pluginsJava)
            dependencies.add("implementation", "${Versions.pluginsGroup}:base-plugins:${Versions.pluginsVersion}")
        }
    }
}