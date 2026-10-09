package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.component.ComponentFactory
import javax.inject.Inject

abstract class DefaultOperatingSystemTargetLibrary @Inject constructor(
    override val target: OperatingSystem,
    private val componentFactory: ComponentFactory
) : DefaultNativeLibrary(target.mainSourceSetName), HasOsTarget {
    private var targets: List<DefaultNativeMachineTargetLibrary>? = null

    override fun visitMachines(visitor: (KotlinTargetMachine) -> Unit) {
        target.machines.forEach(visitor)
    }

    override fun createTargets() {
        val targets = target.machines.map { DefaultNativeMachineTargetLibrary(it) }
        this.targets = targets
        for (target in targets) {
            componentFactory.created(target)
        }
    }

    override fun visitTargets(visitor: (HasMachineTarget) -> Unit) {
        targets!!.forEach(visitor)
    }
}
