package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.machine.info.Machine
import net.rubygrapefruit.plugins.app.NativeMachine

sealed interface HostMachine {
    /**
     * A human-readable string identifier for this machine.
     */
    val alias: String

    fun exeName(name: String) = name

    /**
     * Can this host machine build binaries for the given target machine?
     */
    fun canBuild(target: NativeMachine): Boolean {
        return when (target) {
            NativeMachine.LinuxX64, NativeMachine.WindowsX64 -> true
            else -> false
        }
    }

    val canBeBuilt: Boolean
        get() = true

    val machine: NativeMachine

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

        fun of(machine: NativeMachine): KotlinTargetMachine {
            return when (machine) {
                NativeMachine.WindowsX64 -> WindowsX64
                NativeMachine.LinuxX64 -> LinuxX64
                NativeMachine.MacOSArm64 -> MacOsArm64
            }
        }
    }
}

sealed interface KotlinTargetMachine : HostMachine {
    val kotlinTarget: String
}

sealed class Windows : HostMachine {
    override fun exeName(name: String) = "$name.exe"
}

data object WindowsX64 : Windows(), KotlinTargetMachine {
    override val alias: String
        get() = "windowX64"

    override val machine: NativeMachine
        get() = NativeMachine.WindowsX64

    override val kotlinTarget: String
        get() = "mingwX64"
}

data object WindowsArm64 : Windows() {
    override val alias: String
        get() = "windowsArm64"

    override val machine: NativeMachine
        get() = throw UnsupportedOperationException("Not supported yet")

    override fun canBuild(target: NativeMachine): Boolean {
        return false
    }

    override val canBeBuilt: Boolean = false
}

sealed class Linux : HostMachine

data object LinuxX64 : Linux(), KotlinTargetMachine {
    override val alias: String
        get() = "linuxX64"

    override val machine: NativeMachine
        get() = NativeMachine.LinuxX64

    override val kotlinTarget: String
        get() = "linuxX64"
}

data object LinuxArm64 : Linux() {
    override val alias: String
        get() = "linuxArm64"

    override fun canBuild(target: NativeMachine): Boolean {
        return false
    }

    override val machine: NativeMachine
        get() = throw UnsupportedOperationException("Not supported yet")

    override val canBeBuilt: Boolean = false
}

sealed class MacOS : HostMachine {
    override fun canBuild(target: NativeMachine): Boolean {
        return true
    }
}

data object MacOsX64 : MacOS() {
    override val alias: String
        get() = "macosX64"

    override val machine: NativeMachine
        get() = throw UnsupportedOperationException("Not supported yet")
}

data object MacOsArm64 : MacOS(), KotlinTargetMachine {
    override val alias: String
        get() = "macosArm64"

    override val machine: NativeMachine
        get() = NativeMachine.MacOSArm64

    override val kotlinTarget: String
        get() = "macosArm64"
}
