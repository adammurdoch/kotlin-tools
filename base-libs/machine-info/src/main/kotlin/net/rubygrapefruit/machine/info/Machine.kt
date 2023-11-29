package net.rubygrapefruit.machine.info

import java.io.ByteArrayOutputStream

/**
 * Contains information about the host operating system family and CPU architecture.
 */
sealed class Machine {
    sealed class Windows: Machine()
    data object WindowsX64 : Windows()

    sealed class Linux: Machine()
    data object LinuxX64 : Linux()

    sealed class MacOS : Machine()
    data object MacOSArm64 : MacOS()
    data object MacOSX64 : MacOS()

    companion object {
        val thisMachine by lazy {
            val osName = System.getProperty("os.name")
            if (osName.contains("linux", true)) {
                LinuxX64
            } else if (osName.contains("windows", true)) {
                WindowsX64
            } else if (osName.contains("Mac OS X")) {
                val output = ByteArrayOutputStream()
                val builder = ProcessBuilder("sysctl", "-n", "machdep.cpu.brand_string")
                val process = builder.start()
                process.inputStream.copyTo(output)
                process.errorStream.copyTo(System.err)
                if (output.toString().trim().matches(Regex("Apple M\\d+.*"))) {
                    MacOSArm64
                } else {
                    MacOSX64
                }
            } else {
                throw IllegalStateException("Could not determine the OS family of this machine using OS name '$osName'.")
            }
        }
    }
}

