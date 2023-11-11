package net.rubygrapefruit.plugins.app

import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

interface MultiPlatformLibrary {
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
     * Adds the browser as a target, if not already.
     */
    fun browser()

    /**
     * Configures common dependencies.
     */
    fun common(config: KotlinDependencyHandler.() -> Unit)
}