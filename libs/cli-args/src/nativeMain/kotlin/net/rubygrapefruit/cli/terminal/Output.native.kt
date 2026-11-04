package net.rubygrapefruit.cli.terminal

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.STDERR_FILENO
import platform.posix.fputs
import platform.posix.isatty
import platform.posix.stderr

@OptIn(ExperimentalForeignApi::class)
private object PlainOutput : AbstractPlainOutput() {
    override fun put(text: CharSequence) {
        fputs(text.toString(), stderr)
    }
}

@OptIn(ExperimentalForeignApi::class)
private object TerminalOutput : AbstractOutput() {
    override fun put(text: CharSequence, appearance: Appearance) {
        if (appearance == Appearance.UserInput) {
            // Bold green foreground
            fputs("\u001B[1;32m", stderr)
        }
        fputs(text.toString(), stderr)
        if (appearance == Appearance.UserInput) {
            fputs("\u001B[0m", stderr)
        }
    }
}

private val defaultOutput by lazy {
    val ttyResult = isatty(STDERR_FILENO)
    if (ttyResult == 1) {
        TerminalOutput
    } else {
        PlainOutput
    }
}

actual fun output(): Output = defaultOutput
