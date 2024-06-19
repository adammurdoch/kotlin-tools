package net.rubygrapefruit.cli

import kotlin.system.exitProcess

internal actual fun exit(code: Int): Nothing {
    exitProcess(code)
}