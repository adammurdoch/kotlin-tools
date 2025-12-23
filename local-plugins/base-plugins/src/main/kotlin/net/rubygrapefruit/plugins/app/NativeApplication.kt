package net.rubygrapefruit.plugins.app

import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty

interface NativeApplication : Application, MultiPlatformComponent<Dependencies> {
    /**
     * The main entry point for the application.
     */
    val entryPoint: Property<String>

    /**
     * Adds the native desktops (macOS, Linux, Windows) as a target, if not already.
     */
    fun nativeDesktop()

    /**
     * Adds macOS as a target, if not already.
     */
    fun macOS()

    /**
     * Adds macOS as a target, if not already, and configures it.
     */
    fun macOS(config: NativeComponent<Dependencies>.() -> Unit)

    /**
     * Generated Kotlin source directories for this component.
     */
    val generatedSource: SetProperty<Directory>

    /**
     * The native executables for this application.
     */
    val executables: Provider<List<NativeExecutable>>
}