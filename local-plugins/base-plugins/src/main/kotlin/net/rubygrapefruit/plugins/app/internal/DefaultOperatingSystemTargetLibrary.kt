package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.component.ComponentFactory
import javax.inject.Inject

abstract class DefaultOperatingSystemTargetLibrary @Inject constructor(
    override val target: OperatingSystem,
    private val componentFactory: ComponentFactory
) : DefaultNativeLibrary(target.mainSourceSetName), HasOsTarget {
    override fun visitMachines(visitor: (KotlinTargetMachine) -> Unit) {
        target.machines.forEach(visitor)
    }

    override fun createTargets() {
        for (machine in target.machines) {
            componentFactory.created(DefaultNativeMachineTargetLibrary(machine))
        }
    }
}
