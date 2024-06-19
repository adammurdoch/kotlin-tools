package net.rubygrapefruit.cli.app

import kotlin.system.exitProcess

internal actual fun exit(code: Int): Nothing {
    exitProcess(code)
}