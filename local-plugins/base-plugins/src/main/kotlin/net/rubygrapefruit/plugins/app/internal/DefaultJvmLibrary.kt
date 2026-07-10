package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmLibrary
import net.rubygrapefruit.plugins.app.LibraryDependencies
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import javax.inject.Inject

abstract class DefaultJvmLibrary @Inject constructor(
    mainSourceSetName: String,
    testSourceSetName: String
) : DefaultJvmComponent<LibraryDependencies>(testSourceSetName), JvmLibrary, MutableComponent {
    private val dependencies = DefaultLibraryDependencies()
    override val main = DefaultSourceSet(mainSourceSetName, dependencies, generatedSource)

    override fun dependencies(config: LibraryDependencies.() -> Unit) {
        dependencies.config()
    }
}