package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Provider

interface NativeApplication : Application, MultiPlatformComponent {
    /**
     * Adds the native desktops as a target, if not already.
     */
    fun nativeDesktop()

    /**
     * Adds macOS as a target, if not already.
     */
    fun macOS()

    /**
     * The native executables for this application.
     */
    val executables: Provider<List<NativeExecutable>>

    /**
     * The native executable for this application to use on the current machine, if any.
     */
    val executable: Provider<NativeExecutable>
}