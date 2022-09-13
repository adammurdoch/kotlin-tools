package net.rubygrapefruit.app.internal

internal sealed class Os

internal object Windows: Os()

internal object Linux: Os()

internal object MacOs: Os()


internal fun currentOs(): Os {
    return if (System.getProperty("os.name").contains("linux", true)) {
        Linux
    } else if (System.getProperty("os.name").contains("windows", true)) {
        Windows
    } else {
        MacOs
    }
}
