package net.rubygrapefruit.plugins.app.internal

sealed interface OperatingSystem {
    val mainSourceSetName: String

    data object MacOS : OperatingSystem {
        override val mainSourceSetName: String
            get() = "macosMain"
    }

    data object Linux : OperatingSystem {
        override val mainSourceSetName: String
            get() = "linuxMain"
    }

    data object Windows : OperatingSystem {
        override val mainSourceSetName: String
            get() = "mingwMain"
    }

    companion object {
        val desktop = listOf(MacOS, Linux, Windows)
    }
}