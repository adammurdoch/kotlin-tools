package net.rubygrapefruit.cli.terminal

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
private class PlainOutput(val output: CPointer<FILE>?) : AbstractPlainOutput() {
    override fun put(text: CharSequence) {
        fputs(text.toString(), output)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class TerminalOutput(val output: CPointer<FILE>?) : AbstractOutput() {
    override fun put(text: CharSequence, appearance: Appearance) {
        when (appearance) {
            Appearance.UserInput -> {
                if (text.isEmpty()) {
                    fputs("''", output)
                } else {
                    // Bold green foreground
                    withAttributes("\u001B[1;32m", text)
                }
            }

            Appearance.Heading -> {
                // Bold foreground
                withAttributes("\u001B[1m", text)
            }

            else -> {
                if (text.isNotEmpty()) {
                    fputs(text.toString(), output)
                }
            }
        }
    }

    private fun withAttributes(escape: String, text: CharSequence) {
        if (text.isEmpty()) {
            return
        }
        fputs(escape, output)
        fputs(text.toString(), output)
        fputs("\u001B[0m", output)
    }
}

@OptIn(ExperimentalForeignApi::class)
private val defaultOutput by lazy { outputFor(STDOUT_FILENO, stdout) }

@OptIn(ExperimentalForeignApi::class)
private val defaultDiagnostics by lazy { outputFor(STDERR_FILENO, stderr) }

@OptIn(ExperimentalForeignApi::class)
private fun outputFor(fd: Int, file: CPointer<FILE>?): AbstractOutput {
    val ttyResult = isatty(fd)
    return if (ttyResult == 1) {
        TerminalOutput(file)
    } else {
        PlainOutput(file)
    }
}

actual fun output(): Output = defaultOutput

actual fun diagnostics(): Output = defaultDiagnostics
