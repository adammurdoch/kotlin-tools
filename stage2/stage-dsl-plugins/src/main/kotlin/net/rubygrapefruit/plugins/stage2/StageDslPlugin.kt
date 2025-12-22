package net.rubygrapefruit.plugins.stage2

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

abstract class StageDslPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
    }
}