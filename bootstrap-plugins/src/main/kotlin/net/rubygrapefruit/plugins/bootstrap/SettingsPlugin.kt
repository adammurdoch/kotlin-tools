package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class SettingsPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            gradle.rootProject {
                buildscript.repositories.mavenCentral()
                buildscript.dependencies.add("classpath", Versions.kotlinPluginCoordinates)
            }
        }
    }
}