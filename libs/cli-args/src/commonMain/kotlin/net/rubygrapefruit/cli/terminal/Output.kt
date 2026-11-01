package net.rubygrapefruit.cli.terminal

sealed interface Output {
    /**
     * Writes the given text to the output
     */
    fun write(text: String)

    /**
     * Writes the given text that was provided by the user to the output.
     *
     * May be quoted or rendered with a color.
     */
    fun userInput(text: String)

    /**
     * Writes the given text to the output, followed by line separator.
     */
    fun writeLine(text: String)

    /**
     * Terminates the current line, if not already.
     */
    fun maybeNewLine()
}

/**
 * Returns the default [Output] for this process, connected to the process' stdout and stderr.
 */
expect fun output(): Output
