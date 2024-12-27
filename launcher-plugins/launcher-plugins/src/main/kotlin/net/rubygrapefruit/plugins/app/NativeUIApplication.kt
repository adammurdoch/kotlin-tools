package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Property

interface NativeUIApplication : Application, UiApplication, MultiPlatformComponent<Dependencies> {
    /**
     * The entry point for the application.
     */
    val entryPoint: Property<String>
}
