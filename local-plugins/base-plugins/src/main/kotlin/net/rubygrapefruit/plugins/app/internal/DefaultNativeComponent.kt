package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import javax.inject.Inject

abstract class DefaultNativeComponent @Inject constructor(
    val mainSourceSetName: String
) : NativeComponent<Dependencies>, MutableComponent, HasDependencies, HasGeneratedSource {
    override val dependencies = DefaultDependencies()

    override val sourceSetName: String
        get() = mainSourceSetName

    override fun dependencies(config: Dependencies.() -> Unit) {
        dependencies.config()
    }
}