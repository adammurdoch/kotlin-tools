package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.machine.info.Machine
import net.rubygrapefruit.plugins.app.NativeMachine

sealed class HostMachine {
    companion object {
        val current: HostMachine by lazy {
            val machine: HostMachine = when (Machine.thisMachine) {
                Machine.WindowsX64 -> WindowsX64
                Machine.WindowsArm64 -> WindowsArm64
                Machine.LinuxX64 -> LinuxX64
                Machine.LinuxArm64 -> LinuxArm64
                Machine.MacOSX64 -> MacOsX64
                Machine.MacOSArm64 -> MacOsArm64
            }
            machine
        }

        fun of(machine: NativeMachine): HostMachine {
            return when (machine) {
                NativeMachine.WindowsX64 -> WindowsX64
                NativeMachine.LinuxX64 -> LinuxX64
                NativeMachine.MacOSX64 -> MacOsX64
                NativeMachine.MacOSArm64 -> MacOsArm64
            }
        }
    }

    open fun exeName(name: String) = name

    /**
     * Can this host machine build binaries for the given target machine?
     */
    open fun canBuild(target: NativeMachine): Boolean {
        return when (target) {
            NativeMachine.LinuxX64, NativeMachine.WindowsX64 -> true
            else -> false
        }
    }

    open val canBeBuilt: Boolean = true

    abstract val machine: NativeMachine
}

sealed class Windows : HostMachine() {
    override fun exeName(name: String) = "$name.exe"
}

data object WindowsX64 : Windows() {
    override val machine: NativeMachine
        get() = NativeMachine.WindowsX64
}

data object WindowsArm64 : Windows() {
    override val machine: NativeMachine
        get() = throw UnsupportedOperationException("Not supported yet")

    override fun canBuild(target: NativeMachine): Boolean {
        return false
    }

    override val canBeBuilt: Boolean = false
}

sealed class Linux : HostMachine()

data object LinuxX64 : Linux() {
    override val machine: NativeMachine
        get() = NativeMachine.LinuxX64
}

data object LinuxArm64 : Linux() {
    override fun canBuild(target: NativeMachine): Boolean {
        return false
    }

    override val machine: NativeMachine
        get() = throw UnsupportedOperationException("Not supported yet")

    override val canBeBuilt: Boolean = false
}

sealed class MacOS : HostMachine() {
    override fun canBuild(target: NativeMachine): Boolean {
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
