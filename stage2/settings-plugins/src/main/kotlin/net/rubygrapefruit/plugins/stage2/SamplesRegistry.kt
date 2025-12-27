package net.rubygrapefruit.plugins.stage2

import org.gradle.api.initialization.Settings

abstract class SamplesRegistry(private val settings: Settings) {
    fun jvmCliApp(name: String): JvmCliApp {
        return JvmCliApp(name)
    }

    internal fun validate() {
    }
}