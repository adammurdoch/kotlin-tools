package net.rubygrapefruit.plugins.app.internal

/**
 * A component that targets a specific operating system.
 */
interface HasMachineTarget {
    val target: KotlinTargetMachine
}