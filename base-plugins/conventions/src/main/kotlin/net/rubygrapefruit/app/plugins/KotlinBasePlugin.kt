package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.settingsPluginApplied
import net.rubygrapefruit.plugins.bootstrap.Versions
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.util.concurrent.locks.ReentrantLock

class KotlinBasePlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        with(target) {
            target.gradle.rootProject { project ->
                with(project) {

                    buildscript.repositories.mavenCentral()
                    buildscript.dependencies.add("classpath", Versions.kotlinPluginCoordinates)

                    // Need this to resolve native tooling
                    repositories.mavenCentral()

                    settingsPluginApplied()
                }
            }
        }
    }
}