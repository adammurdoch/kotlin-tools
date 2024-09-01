package net.rubygrapefruit.machine.info

import net.rubygrapefruit.machine.cpu.Arch

/**
 * Contains information about the host operating system family and CPU architecture.
 */
sealed class Machine {
    sealed class Windows : Machine()
    data object WindowsX64 : Windows()

    sealed class Linux : Machine()
    data object LinuxX64 : Linux()
    data object LinuxArm64 : Linux()

    sealed class MacOS : Machine()
    data object MacOSArm64 : MacOS()
    data object MacOSX64 : MacOS()

    companion object {
        val thisMachine by lazy {
            val osName = System.getProperty("os.name")
            val machine = if (osName.contains("linux", true)) {
                if (System.getProperty("os.arch") == "aarch64") {
                    LinuxArm64
                } else {
                    LinuxX64
                }
            } else if (osName.contains("windows", true)) {
                WindowsX64
            } else if (osName.contains("Mac OS X")) {
                val architecture = Arch.getArchitecture()
                println("-> HOST ARCH: " + architecture);
                if (architecture.startsWith("Apple")) {
                    MacOSArm64
                } else if (architecture.startsWith("Intel")){
                    MacOSX64
                } else {
                    throw IllegalStateException("Could not determine machine architecture using machine '$architecture'.");
                }
            } else {
                throw IllegalStateException("Could not determine the OS family of this machine using OS name '$osName'.")
            }
            println("-> HOST MACHINE: $machine")
            machine
        }
    }
}

