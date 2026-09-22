package net.rubygrapefruit.plugins.app

import org.gradle.api.file.Directory
import org.gradle.api.provider.SetProperty

interface PluginBundle {
    /**
     * Adds a plugin implementation.
     */
    fun plugin(id: String, implementation: String)

    /**
     * Configures production dependencies for this bundle.
     */
    fun dependencies(config: LibraryDependencies.() -> Unit)

    /**
     * Generated Kotlin source directories for this component.
     */
    val generatedSource: SetProperty<Directory>
}