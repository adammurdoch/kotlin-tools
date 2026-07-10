package net.rubygrapefruit.plugins.app

import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

interface NativeUIApplication : Application, UiApplication, MultiPlatformComponent<Dependencies> {
    /**
     * The main entry point for the application.
     */
    val entryPoint: Property<String>

    /**
     * Adds macOS as a target, if not already, and configures it.
     */
    fun macOS(config: NativeComponent<Dependencies>.() -> Unit)

    /**
     * Generated Kotlin source directories for this component.
     */
    val generatedSource: SetProperty<Directory>
}
