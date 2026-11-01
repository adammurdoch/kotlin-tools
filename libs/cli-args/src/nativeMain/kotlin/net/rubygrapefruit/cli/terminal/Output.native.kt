package net.rubygrapefruit.cli.terminal

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fputs
import platform.posix.isatty
import platform.posix.stderr

@OptIn(ExperimentalForeignApi::class)
private object DefaultOutput : AbstractOutput() {
    private val terminal = isatty(1) == 1

    override fun put(text: CharSequence, appearance: Appearance) {
        if (terminal && appearance== Appearance.UserInput) {
            fputs("\u001B[32m", stderr)
        }
        fputs(text.toString(), stderr)
        if (terminal && appearance== Appearance.UserInput) {
            fputs("\u001B[0m", stderr)
        }
    }
}

actual fun output(): Output = DefaultOutput