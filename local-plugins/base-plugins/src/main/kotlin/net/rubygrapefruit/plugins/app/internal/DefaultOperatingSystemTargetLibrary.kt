package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.component.ComponentFactory
import javax.inject.Inject

abstract class DefaultOperatingSystemTargetLibrary @Inject constructor(
    override val target: OperatingSystem,
    private val componentFactory: ComponentFactory
) : DefaultNativeLibrary(target.mainSourceSetName), HasOsTarget {
    private val machineTargets = target.machines.map { componentFactory.created(DefaultNativeMachineTargetLibrary(it)) }

    override fun visitMachines(visitor: (HasMachineTarget) -> Unit) {
        machineTargets.forEach(visitor)
    }
}
