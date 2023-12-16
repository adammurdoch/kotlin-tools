package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import net.rubygrapefruit.plugins.app.Versions

class SettingsPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            gradle.rootProject { project ->
                project.run {
                    buildscript.repositories.mavenCentral()
                    buildscript.dependencies.add("classpath", Versions.kotlinPluginCoordinates)
                }
            }
        }
    }
}