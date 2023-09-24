package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.settingsPluginApplied
import net.rubygrapefruit.plugins.bootstrap.SettingsPlugin
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class KotlinBasePlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            plugins.apply(SettingsPlugin::class.java)
            gradle.rootProject { project ->
                project.run {
                    // Need this to resolve native tooling
                    repositories.mavenCentral()

                    settingsPluginApplied()
                }
            }
        }
    }
}