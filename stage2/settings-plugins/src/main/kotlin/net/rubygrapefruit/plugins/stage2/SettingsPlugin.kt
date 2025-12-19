package net.rubygrapefruit.plugins.stage2

import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

@Suppress("unused")
class SettingsPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            plugins.apply(BuildConstants.constants.foojay.plugin.id)
            gradle.rootProject { project ->
                project.run {
                    buildscript.repositories.mavenCentral()
                    buildscript.dependencies.add("classpath", BuildConstants.constants.kotlin.plugin.coordinates)
                    buildscript.dependencies.add("classpath", BuildConstants.constants.serialization.plugin.coordinates)

                    // For commonization
                    repositories.mavenCentral()
                }
            }
        }
    }
}