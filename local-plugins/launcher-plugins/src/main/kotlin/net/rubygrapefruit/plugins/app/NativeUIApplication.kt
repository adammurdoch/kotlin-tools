package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Property

interface NativeUIApplication : Application, UiApplication, MultiPlatformComponent<Dependencies> {
    /**
     * The main entry point for the application.
     */
    val entryPoint: Property<String>

    /**
     * Adds macOS as a target, if not already, and configures it.
     */
    fun macOS(config: NativeComponent<Dependencies>.() -> Unit)
}
