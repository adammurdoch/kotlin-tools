package net.rubygrapefruit.plugins.app

import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

interface NativeLibrary {
    /**
     * Configures production dependencies for this component.
     */
    fun dependencies(config: KotlinDependencyHandler.() -> Unit)
}