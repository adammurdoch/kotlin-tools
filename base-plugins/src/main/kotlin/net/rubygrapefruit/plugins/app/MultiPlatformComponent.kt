package net.rubygrapefruit.plugins.app

import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

interface MultiPlatformComponent {
    /**
     * Configures common dependencies for this component.
     */
    fun common(config: KotlinDependencyHandler.() -> Unit)

    /**
     * Configures common test dependencies for this component.
     */
    fun test(config: KotlinDependencyHandler.() -> Unit)
}