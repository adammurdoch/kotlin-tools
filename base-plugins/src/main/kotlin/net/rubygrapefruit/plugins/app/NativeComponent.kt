package net.rubygrapefruit.plugins.app

import org.gradle.api.file.Directory
import org.gradle.api.provider.SetProperty

interface NativeComponent<D : Dependencies> {
    /**
     * Configures production dependencies for this component.
     */
    fun dependencies(config: D.() -> Unit)

    /**
     * Generated Kotlin source directories for this component.
     */
    val generatedSource: SetProperty<Directory>
}