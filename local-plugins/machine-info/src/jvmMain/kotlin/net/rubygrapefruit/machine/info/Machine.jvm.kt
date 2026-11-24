package net.rubygrapefruit.machine.info

import net.rubygrapefruit.machine.cpu.Arch
import net.rubygrapefruit.machine.info.Machine.*

internal actual fun thisMachine(): Machine {
    val osName = System.getProperty("os.name")
    val machine = if (osName.contains("linux", true)) {
        val architecture = System.getProperty("os.arch")
        if (architecture == "aarch64") {
            LinuxArm64
        } else if (architecture == "amd64") {
            LinuxX64
        } else {
            throw IllegalStateException("Could not determine machine architecture using machine '$architecture'.")
        }
    } else if (osName.contains("windows", true)) {
        val architecture = System.getProperty("os.arch")
        if (architecture == "aarch64") {
            WindowsArm64
        } else if (architecture == "amd64") {
            WindowsX64
        } else {
            throw IllegalStateException("Could not determine machine architecture using machine '$architecture'.")
        }
    } else if (osName.contains("Mac OS X")) {
        val architecture = Arch.getMacOsArchitecture()
        if (architecture.startsWith("Apple")) {
            MacOSArm64
        } else if (architecture.startsWith("Intel")) {
            MacOSX64
        } else {
            throw IllegalStateException("Could not determine machine architecture using machine '$architecture'.")
        }
    } else {
        throw IllegalStateException("Could not determine the OS family of this machine using OS name '$osName'.")
    }
    return machine
}