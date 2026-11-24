package net.rubygrapefruit.plugins.stage2

import org.gradle.api.provider.Property

abstract class JvmLibrary {
    val dependencies = LibraryDependencies()
    abstract val targetJvmVersion: Property<Int>

    fun dependencies(config: LibraryDependencies.() -> Unit) {
        dependencies.config()
    }
}