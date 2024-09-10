package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Property

interface JvmComponent<D: Dependencies> {
    /**
     * The target Java version for this component.
     */
    val targetJavaVersion: Property<Int>

    /**
     * Configures production dependencies for this component.
     */
    fun dependencies(config: D.() -> Unit)

    /**
     * Configures test dependencies for this component.
     */
    fun test(config: D.() -> Unit)
}