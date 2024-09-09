package net.rubygrapefruit.plugins.app

interface NativeLibrary {
    /**
     * Configures production dependencies for this component.
     */
    fun dependencies(config: Dependencies.() -> Unit)

    interface Dependencies {
        fun api(dependencyNotation: Any)
        fun implementation(dependencyNotation: Any)
    }
}