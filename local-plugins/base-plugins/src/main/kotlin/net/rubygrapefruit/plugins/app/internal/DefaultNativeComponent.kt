package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import javax.inject.Inject

abstract class DefaultNativeComponent @Inject constructor(
    mainSourceSetName: String
) : NativeComponent<Dependencies>, MutableComponent, PlatformContribution {
    override val main = DefaultSourceSet(mainSourceSetName, generatedSource)

    override fun dependencies(config: Dependencies.() -> Unit) {
        main.dependencies.config()
    }
}