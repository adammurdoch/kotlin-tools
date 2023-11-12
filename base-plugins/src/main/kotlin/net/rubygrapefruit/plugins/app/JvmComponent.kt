package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Property
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

interface JvmComponent {
    /**
     * The target Java version for this component.
     */
    val targetJavaVersion: Property<Int>

    /**
     * Configures production dependencies for this component.
     */
    fun dependencies(config: KotlinDependencyHandler.() -> Unit)

    /**
     * Configures tje test dependencies for this component.
     */
    fun test(config: KotlinDependencyHandler.() -> Unit)
}