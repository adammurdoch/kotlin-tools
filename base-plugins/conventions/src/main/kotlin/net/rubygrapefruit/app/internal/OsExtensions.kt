package net.rubygrapefruit.app.internal

sealed class Os

object Windows: Os()

object Linux: Os()

object MacOs: Os()


fun currentOs(): Os {
    return if (System.getProperty("os.name").contains("linux", true)) {
        Linux
    } else if (System.getProperty("os.name").contains("windows", true)) {
        Windows
    } else {
        MacOs
    }
}
