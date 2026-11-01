package net.rubygrapefruit.cli.terminal

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fputs
import platform.posix.stderr

@OptIn(ExperimentalForeignApi::class)
private object DefaultOutput : AbstractOutput() {
    override fun put(text: CharSequence) {
        fputs(text.toString(), stderr)
    }
}

actual fun output(): Output = DefaultOutput