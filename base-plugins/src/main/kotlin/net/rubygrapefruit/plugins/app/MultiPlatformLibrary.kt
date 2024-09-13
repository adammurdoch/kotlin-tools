package net.rubygrapefruit.plugins.app

interface MultiPlatformLibrary : MultiPlatformComponent<LibraryDependencies> {
    /**
     * Adds the JVM as a target, if not already, using the defaults.
     */
    fun jvm()

    /**
     * Adds the JVM as a target, if not already, and applies the given configuration.
     */
    fun jvm(config: JvmLibrary.() -> Unit)

    /**
     * Adds all desktops targets (JVM, macOS, Linux, Windows), if not already.
     */
    fun desktop()

    /**
     * Adds all desktops targets (JVM, macOS, Linux, Windows), if not already, and applies the given configuration.
     */
    fun desktop(config: NativeLibrary.() -> Unit)

    /**
     * Adds the native desktops (macOS, Linux, Windows) as a target, if not already.
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