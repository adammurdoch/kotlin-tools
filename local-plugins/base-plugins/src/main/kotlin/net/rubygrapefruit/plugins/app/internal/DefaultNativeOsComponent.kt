package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import javax.inject.Inject

abstract class DefaultNativeOsComponent @Inject constructor(
    override val target: OperatingSystem
) : NativeComponent<Dependencies>, MutableComponent, PlatformContribution, HasOsTarget {
    override val main = DefaultSourceSet(target.mainSourceSetName, generatedSource)

    override fun dependencies(config: Dependencies.() -> Unit) {
        main.dependencies.config()
    }
}