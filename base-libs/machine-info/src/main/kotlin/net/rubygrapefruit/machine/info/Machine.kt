package net.rubygrapefruit.machine.info

import java.io.ByteArrayOutputStream

/**
 * Contains information about the host operating system family and CPU architecture.
 */
sealed class Machine {
    sealed class Windows: Machine()
    object WindowsX64 : Windows()

    sealed class Linux: Machine()
    object LinuxX64 : Linux()

    sealed class MacOSMachine : Machine()
    object MacOSArm64 : MacOSMachine()
    object MacOSX64 : MacOSMachine()

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
                if (output.toString().contains("Apple M1")) {
                    MacOSArm64
                } else {
                    MacOSX64
                }
            } else {
                throw IllegalStateException("Could not determine the OS family of this machine from OS name '$osName'.")
            }
        }
    }
}

