package net.rubygrapefruit.plugins.stage2

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

@Suppress("unused")
abstract class TestAppsPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            val extension = extensions.create("samples", SamplesRegistry::class.java, target)
            target.gradle.rootProject { rootProject ->
                extension.applyTo(rootProject)
            }
        }
    }
}