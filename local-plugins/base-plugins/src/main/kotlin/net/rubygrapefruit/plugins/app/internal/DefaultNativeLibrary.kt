package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.LibraryDependencies
import net.rubygrapefruit.plugins.app.NativeLibrary
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import javax.inject.Inject

abstract class DefaultNativeLibrary @Inject constructor(
    val mainSourceSetName: String
) : NativeLibrary, MutableComponent, HasGeneratedSource {
    val dependencies = DefaultLibraryDependencies()

    override val sourceSetName: String
        get() = mainSourceSetName

    override fun dependencies(config: LibraryDependencies.() -> Unit) {
        dependencies.config()
    }
}