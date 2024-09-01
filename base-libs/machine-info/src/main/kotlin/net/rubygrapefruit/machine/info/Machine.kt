package net.rubygrapefruit.machine.info

import net.rubygrapefruit.machine.cpu.Arch
import net.rubygrapefruit.machine.info.OperatingSystem.Windows

sealed interface OperatingSystem {
    fun executableName(name: String) = name
    fun scriptName(name: String) = name

    data object Windows : OperatingSystem {
        override fun executableName(name: String) = "$name.exe"

        override fun scriptName(name: String) = "$name.bat"
    }

    data object Linux : OperatingSystem
    data object MacOS : OperatingSystem
}

sealed interface Architecture {
    data object X64 : Architecture
    data object Arm64 : Architecture
}

/**
 * Contains information about the host operating system family and CPU architecture.
 */
sealed class Machine(
    val operatingSystem: OperatingSystem,
    val architecture: Architecture
) {
    data object WindowsX64 : Machine(OperatingSystem.Windows, Architecture.X64)
    data object WindowsArm64 : Machine(OperatingSystem.Windows, Architecture.Arm64)

    data object LinuxX64 : Machine(OperatingSystem.Linux, Architecture.X64)
    data object LinuxArm64 : Machine(OperatingSystem.Linux, Architecture.Arm64)

    data object MacOSArm64 : Machine(OperatingSystem.MacOS, Architecture.Arm64)
    data object MacOSX64 : Machine(OperatingSystem.MacOS, Architecture.X64)

    fun executableName(name: String) = operatingSystem.executableName(name)
    fun scriptName(name: String) = operatingSystem.scriptName(name)

    val isWindows: Boolean
        get() = operatingSystem is Windows

    val isMacOS: Boolean
        get() = operatingSystem is OperatingSystem.MacOS

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
                } else if (architecture == "amd64") {
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

