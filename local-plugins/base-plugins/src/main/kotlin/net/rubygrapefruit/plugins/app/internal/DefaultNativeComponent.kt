package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import javax.inject.Inject

abstract class DefaultNativeComponent @Inject constructor(
    mainSourceSetName: String
) : NativeComponent<Dependencies>, MutableComponent, PlatformContribution {
    private val dependencies = DefaultDependencies()
    override val main = DefaultSourceSet(mainSourceSetName, dependencies, generatedSource)

    override fun dependencies(config: Dependencies.() -> Unit) {
        dependencies.config()
    }
}