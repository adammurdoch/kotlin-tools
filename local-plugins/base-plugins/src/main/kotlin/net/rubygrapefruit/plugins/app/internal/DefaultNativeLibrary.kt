package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.LibraryDependencies
import net.rubygrapefruit.plugins.app.NativeLibrary
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import javax.inject.Inject

abstract class DefaultNativeLibrary @Inject constructor(
    mainSourceSetName: String
) : NativeLibrary, MutableComponent, PlatformContribution {
    override val main = DefaultLibrarySourceSet(mainSourceSetName, generatedSource)

    override fun dependencies(config: LibraryDependencies.() -> Unit) {
        main.dependencies.config()
    }
}