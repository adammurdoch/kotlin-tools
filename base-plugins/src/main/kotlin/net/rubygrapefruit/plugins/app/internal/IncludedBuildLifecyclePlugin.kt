package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.bootstrap.IncludedBuildPlugin
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class IncludedBuildLifecyclePlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            plugins.apply(IncludedBuildPlugin::class.java)
        }
    }
}