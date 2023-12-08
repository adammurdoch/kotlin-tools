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
     * The output binary for the host machine.
     */
    val outputBinary: Provider<RegularFile>

    /**
     * The output binary for the given machine, if buildable.
     */
    fun outputBinary(target: NativeMachine): Provider<RegularFile>
}