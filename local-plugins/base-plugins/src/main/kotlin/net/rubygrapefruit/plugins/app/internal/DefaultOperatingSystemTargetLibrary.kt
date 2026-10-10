package net.rubygrapefruit.plugins.app.internal

import javax.inject.Inject

abstract class DefaultOperatingSystemTargetLibrary @Inject constructor(
    override val target: OperatingSystem
) : DefaultNativeLibrary(target.mainSourceSetName), HasOsTarget {
    private val targets = target.machines.map { DefaultNativeMachineTargetLibrary(it) }

    override fun visitTargets(visitor: (HasMachineTarget) -> Unit) {
        targets.forEach(visitor)
    }
}
