package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.machine.info.Machine
import net.rubygrapefruit.plugins.app.NativeMachine

sealed class HostMachine {
    companion object {
        val current: HostMachine by lazy {
            when (Machine.thisMachine) {
                Machine.WindowsX64 -> WindowsX64
                Machine.LinuxX64 -> LinuxX64
                Machine.MacOSX64 -> MacOsX64
                Machine.MacOSArm64 -> MacOsArm64
            }
        }
    }

    open fun exeName(name: String) = name

    open fun canBuild(machine: NativeMachine): Boolean {
        return when (machine) {
            NativeMachine.LinuxX64, NativeMachine.WindowsX64 -> true
            else -> false
        }
    }

    abstract val machine: NativeMachine
}

sealed class Windows : HostMachine() {
    override fun exeName(name: String) = "$name.exec"
}

data object WindowsX64 : Windows() {
    override val machine: NativeMachine
        get() = NativeMachine.WindowsX64
}

sealed class Linux : HostMachine()

data object LinuxX64 : Linux() {
    override val machine: NativeMachine
        get() = NativeMachine.LinuxX64
}

sealed class MacOS : HostMachine() {
    override fun canBuild(machine: NativeMachine): Boolean {
        return true
    }
}

data object MacOsX64 : MacOS() {
    override val machine: NativeMachine
        get() = NativeMachine.MacOSX64
}

data object MacOsArm64 : MacOS() {
    override val machine: NativeMachine
        get() = NativeMachine.MacOSArm64
}
