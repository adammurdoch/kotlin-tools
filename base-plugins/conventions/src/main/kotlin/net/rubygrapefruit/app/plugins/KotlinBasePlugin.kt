package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.settingsPluginApplied
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class KotlinBasePlugin: Plugin<Settings> {
    override fun apply(target: Settings) {
        with(target) {
            target.gradle.rootProject { project ->
                with(project) {
                    buildscript.repositories.mavenCentral()
                    buildscript.dependencies.add("classpath", "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")

                    // Need this to resolve native tooling
                    repositories.mavenCentral()

                    settingsPluginApplied()
                }
            }
        }
    }
}