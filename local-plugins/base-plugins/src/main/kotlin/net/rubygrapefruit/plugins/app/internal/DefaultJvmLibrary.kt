package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmLibrary
import net.rubygrapefruit.plugins.app.LibraryDependencies
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import javax.inject.Inject

abstract class DefaultJvmLibrary @Inject constructor(
    mainSourceSetName: String,
    testSourceSetName: String
) : DefaultJvmComponent<LibraryDependencies>(mainSourceSetName, testSourceSetName), JvmLibrary, MutableComponent {
    override val dependencies = DefaultLibraryDependencies()

    override fun dependencies(config: LibraryDependencies.() -> Unit) {
        dependencies.config()
    }
}