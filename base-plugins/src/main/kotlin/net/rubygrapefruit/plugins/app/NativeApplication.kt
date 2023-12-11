package net.rubygrapefruit.plugins.app

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
}