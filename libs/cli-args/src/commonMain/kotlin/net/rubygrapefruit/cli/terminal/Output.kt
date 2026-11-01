package net.rubygrapefruit.cli.terminal

sealed interface Output {
    fun append(text: String)

    fun appendLine(text: String)

    fun maybeNewLine()
}

/**
 * Returns the default [Output] for this process, connected to the process' stdout and stderr.
 */
fun output(): Output = DefaultOutput
