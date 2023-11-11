package net.rubygrapefruit.plugins.app

interface MultiPlatformLibrary {
    /**
     * Adds the JVM as a target, using the defaults.
     */
    fun jvm()

    fun jvm(body: JvmLibrary.() -> Unit)

    fun nativeDesktop()
}