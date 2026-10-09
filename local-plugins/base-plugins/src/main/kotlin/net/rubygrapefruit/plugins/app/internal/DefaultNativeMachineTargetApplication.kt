package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.strings.capitalized

class DefaultNativeMachineTargetApplication(override val target: KotlinTargetMachine) : HasMachineTarget, HasMainFunction {
    override val kspConfigurationName: String
        get() = "ksp${target.kotlinTarget.capitalized()}"

    override val kspTaskName: String
        get() = "kspKotlin${target.kotlinTarget.capitalized()}"
}