package net.rubygrapefruit.plugins.app.internal

sealed interface OperatingSystem {
    val mainSourceSetName: String

    val machines: List<KotlinTargetMachine>

    data object MacOS : OperatingSystem {
        override val mainSourceSetName: String
            get() = "macosMain"

        override val machines: List<KotlinTargetMachine>
            get() = listOf(MacOsArm64)
    }

    data object Linux : OperatingSystem {
        override val mainSourceSetName: String
            get() = "linuxMain"

        override val machines: List<KotlinTargetMachine>
            get() = listOf(LinuxX64)
    }

    data object Windows : OperatingSystem {
        override val mainSourceSetName: String
            get() = "mingwMain"

        override val machines: List<KotlinTargetMachine>
            get() = listOf(WindowsX64)
    }

    companion object {
        val desktop = listOf(MacOS, Linux, Windows)
    }
}