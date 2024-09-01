package net.rubygrapefruit.machine.info

import net.rubygrapefruit.machine.cpu.Arch

/**
 * Contains information about the host operating system family and CPU architecture.
 */
sealed class Machine {
    sealed class Windows : Machine() {
        override fun executableName(name: String) = "$name.exe"
    }

    data object WindowsX64 : Windows()
    data object WindowsArm64 : Windows()

    sealed class Linux : Machine()
    data object LinuxX64 : Linux()
    data object LinuxArm64 : Linux()

    sealed class MacOS : Machine()
    data object MacOSArm64 : MacOS()
    data object MacOSX64 : MacOS()

    open fun executableName(name: String) = name

    companion object {
        val thisMachine by lazy {
            val osName = System.getProperty("os.name")
            val machine = if (osName.contains("linux", true)) {
                val architecture = System.getProperty("os.arch")
                println("-> HOST ARCH: $architecture")
                if (architecture == "aarch64") {
                    LinuxArm64
                } else if (architecture == "amd64") {
                    LinuxX64
                } else {
                    throw IllegalStateException("Could not determine machine architecture using machine '$architecture'.");
                }
            } else if (osName.contains("windows", true)) {
                val architecture = System.getProperty("os.arch")
                println("-> HOST ARCH: $architecture")
                if (architecture == "aarch64") {
                    WindowsArm64
                } else if (architecture == "x64") {
                    WindowsX64
                } else {
                    throw IllegalStateException("Could not determine machine architecture using machine '$architecture'.");
                }
            } else if (osName.contains("Mac OS X")) {
                val architecture = Arch.getMacOsArchitecture()
                println("-> HOST ARCH: $architecture")
                if (architecture.startsWith("Apple")) {
                    MacOSArm64
                } else if (architecture.startsWith("Intel")) {
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

