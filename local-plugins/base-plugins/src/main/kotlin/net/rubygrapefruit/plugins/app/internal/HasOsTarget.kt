package net.rubygrapefruit.plugins.app.internal

/**
 * A component that targets a specific operating system.
 */
interface HasOsTarget {
    val target: OperatingSystem

    fun visitMachines(visitor: (KotlinTargetMachine) -> Unit)

    /**
     * Create and register native machine targets for this OS. This is to allow eager configuration for the associated Kotlin target.
     */
    fun createTargets()

    fun visitTargets(visitor: (HasMachineTarget) -> Unit)
}