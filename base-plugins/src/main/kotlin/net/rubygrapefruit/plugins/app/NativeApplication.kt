package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Provider

interface NativeApplication : Application, MultiPlatformComponent<Dependencies> {
    /**
     * Adds the native desktops (macOS, Linux, Windows) as a target, if not already.
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
}