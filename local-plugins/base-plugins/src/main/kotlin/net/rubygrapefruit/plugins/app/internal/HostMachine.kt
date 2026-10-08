package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.machine.info.Machine

sealed interface HostMachine {
    /**
     * A human-readable string identifier for this machine.
     */
    val alias: String

    fun exeName(name: String) = name

    /**
     * Can this host machine build binaries for the given target machine?
     */
    fun canBuild(target: KotlinTargetMachine): Boolean {
        return when (target) {
            // Every host can build Linux and windows binaries
            LinuxX64, WindowsX64 -> true
            else -> false
        }
    }

    companion object {
        val current: KotlinTargetMachine by lazy {
            when (Machine.thisMachine) {
                Machine.WindowsX64 -> WindowsX64
                Machine.LinuxX64 -> LinuxX64
                Machine.LinuxArm64 -> LinuxArm64
                Machine.MacOSX64 -> MacOsX64
                Machine.MacOSArm64 -> MacOsArm64
                Machine.WindowsArm64 -> throw IllegalStateException()
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

    override val kotlinTarget: String
        get() = "mingwX64"
}

sealed class Linux : HostMachine

data object LinuxX64 : Linux(), KotlinTargetMachine {
    override val alias: String
        get() = "linuxX64"

    override val kotlinTarget: String
        get() = "linuxX64"
}

data object LinuxArm64 : Linux(), KotlinTargetMachine {
    override val alias: String
        get() = "linuxArm64"

    override val kotlinTarget: String
        get() = "linuxArm64"
}

sealed class MacOS : HostMachine {
    override fun canBuild(target: KotlinTargetMachine): Boolean {
        // macOS hosts can build all targets
        return true
    }
}

data object MacOsX64 : MacOS(), KotlinTargetMachine {
    override val alias: String
        get() = "macosX64"

    override val kotlinTarget: String
        get() = "macosX64"
}

data object MacOsArm64 : MacOS(), KotlinTargetMachine {
    override val alias: String
        get() = "macosArm64"

    override val kotlinTarget: String
        get() = "macosArm64"
}
