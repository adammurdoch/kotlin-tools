package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine

sealed interface OperatingSystem {
    val mainSourceSetName: String

    val machines: List<NativeMachine>

    data object MacOS : OperatingSystem {
        override val mainSourceSetName: String
            get() = "macosMain"

        override val machines: List<NativeMachine>
            get() = listOf(NativeMachine.MacOSArm64)
    }

    data object Linux : OperatingSystem {
        override val mainSourceSetName: String
            get() = "linuxMain"

        override val machines: List<NativeMachine>
            get() = listOf(NativeMachine.LinuxX64)
    }

    data object Windows : OperatingSystem {
        override val mainSourceSetName: String
            get() = "mingwMain"

        override val machines: List<NativeMachine>
            get() = listOf(NativeMachine.WindowsX64)
    }

    companion object {
        val desktop = listOf(MacOS, Linux, Windows)
    }
}