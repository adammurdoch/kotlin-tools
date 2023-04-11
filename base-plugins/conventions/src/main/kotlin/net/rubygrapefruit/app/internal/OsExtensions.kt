package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeMachine
import net.rubygrapefruit.machine.info.Machine

sealed class Os {
    open fun exeName(name: String) = name

    open fun canBuild(machine: NativeMachine): Boolean {
        return when (machine) {
            NativeMachine.LinuxX64, NativeMachine.WindowsX64 -> true
            else -> false
        }
    }

    abstract val machine: NativeMachine
}

object Windows : Os() {
    override fun exeName(name: String) = "$name.exec"

    override val machine: NativeMachine
        get() = NativeMachine.WindowsX64
}

object Linux : Os() {
    override val machine: NativeMachine
        get() = NativeMachine.LinuxX64
}

object MacOsX64 : Os() {
    override fun canBuild(machine: NativeMachine): Boolean {
        return true
    }

    override val machine: NativeMachine
        get() = NativeMachine.MacOSX64
}

object MacOsArm64 : Os() {
    override fun canBuild(machine: NativeMachine): Boolean {
        return true
    }

    override val machine: NativeMachine
        get() = NativeMachine.MacOSArm64
}

val currentOs: Os by lazy {
    val machine = Machine.thisMachine
    when (machine) {
        Machine.WindowsX64 -> Windows
        Machine.LinuxX64 -> Linux
        Machine.MacOSX64 -> MacOsX64
        Machine.MacOSArm64 -> MacOsArm64
    }.also {
        println("-> CURRENT MACHINE: $it")
    }
}
