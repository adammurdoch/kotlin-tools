package net.rubygrapefruit.app.launcher

import kotlin.system.exitProcess

actual fun exit(status: Int): Nothing {
    exitProcess(status)
}