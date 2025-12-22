package net.rubygrapefruit.plugins.stage2

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

@Suppress("unused")
abstract class StageDslPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            extensions.create("projects", ProjectBuilder::class.java, target)
        }
    }
}