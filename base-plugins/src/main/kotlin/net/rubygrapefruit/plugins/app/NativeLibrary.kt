package net.rubygrapefruit.plugins.app

interface NativeLibrary {
    /**
     * Configures production dependencies for this component.
     */
    fun dependencies(config: LibraryDependencies.() -> Unit)
}