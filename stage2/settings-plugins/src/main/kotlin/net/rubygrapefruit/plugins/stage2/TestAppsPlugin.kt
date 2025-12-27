package net.rubygrapefruit.plugins.stage2

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

@Suppress("unused")
abstract class TestAppsPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            extensions.create("samples", SamplesRegistry::class.java)
            for (f in rootDir.listFiles()) {
                val ignore = listOf(".gradle", ".kotlin", "gradle", "kotlin-js-store")
                if (f.isDirectory && !ignore.contains(f.name)) {
                    include(f.name)
                }
            }
        }
    }
}