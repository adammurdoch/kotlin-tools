package net.rubygrapefruit.plugins.app

interface MultiPlatformLibrary : MultiPlatformComponent {
    /**
     * Adds the JVM as a target, if not already, using the defaults.
     */
    fun jvm()

    /**
     * Adds the JVM as a target, if not already, and applies the given configuration.
     */
    fun jvm(config: JvmLibrary.() -> Unit)

    /**
     * Adds the native desktops as a target, if not already.
     */
    fun nativeDesktop()

    /**
     * Adds macOS as a target, if not already.
     */
    fun macOS()

    /**
     * Adds macOS as a target, if not already, and applies the given configuration.
     */
    fun macOS(config: NativeLibrary.() -> Unit)

    /**
     * Adds the browser as a target, if not already.
     */
    fun browser()
}