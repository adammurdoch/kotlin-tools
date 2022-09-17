package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeMachine
import java.io.ByteArrayOutputStream

sealed class Os {
    open fun exeName(name: String) = name

    open fun canBuild(machine: NativeMachine): Boolean {
        return when (machine) {
            NativeMachine.LinuxX64, NativeMachine.WindowsX64 -> true
            else -> false
        }
    }

    abstract val machine: NativeMachine
}

object Windows : Os() {
    override fun exeName(name: String) = "$name.exec"

    override val machine: NativeMachine
        get() = NativeMachine.WindowsX64
}

object Linux : Os() {
    override val machine: NativeMachine
        get() = NativeMachine.LinuxX64
}

object MacOs : Os() {
    override fun canBuild(machine: NativeMachine): Boolean {
        return true
    }

    override val machine: NativeMachine by lazy {
        val output = ByteArrayOutputStream()
        val builder = ProcessBuilder("sysctl", "-n", "machdep.cpu.brand_string")
        val process = builder.start()
        process.inputStream.copyTo(output)
        process.errorStream.copyTo(System.err)
        if (output.toString().contains("Apple M1")) {
            NativeMachine.MacOSArm64
        } else {
            NativeMachine.MacOSX64
        }
    }
}

val currentOs: Os by lazy {
    if (System.getProperty("os.name").contains("linux", true)) {
        Linux
    } else if (System.getProperty("os.name").contains("windows", true)) {
        Windows
    } else {
        MacOs
    }
}
