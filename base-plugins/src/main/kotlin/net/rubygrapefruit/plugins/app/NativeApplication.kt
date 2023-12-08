package net.rubygrapefruit.plugins.app

import org.gradle.api.file.RegularFile
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
     * The current set of native executables for this application.
     */
    val executables: List<NativeExecutable>

    /**
     * The output binary for the host machine, if any.
     */
    val outputBinary: Provider<RegularFile>
}