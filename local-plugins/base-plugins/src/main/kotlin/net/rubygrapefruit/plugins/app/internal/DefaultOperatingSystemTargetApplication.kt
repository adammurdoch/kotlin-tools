package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import javax.inject.Inject

abstract class DefaultOperatingSystemTargetApplication @Inject constructor(
    override val target: OperatingSystem
) : NativeComponent<Dependencies>, MutableComponent, PlatformContribution, HasOsTarget {
    override val main = DefaultSourceSet(target.mainSourceSetName, generatedSource)
    private val targets = target.machines.map { DefaultNativeMachineTargetApplication(it) }

    override fun dependencies(config: Dependencies.() -> Unit) {
        main.dependencies.config()
    }

    override fun visitTargets(visitor: (HasMachineTarget) -> Unit) {
        targets.forEach(visitor)
    }
}