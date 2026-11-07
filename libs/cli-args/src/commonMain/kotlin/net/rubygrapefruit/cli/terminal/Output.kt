package net.rubygrapefruit.cli.terminal

/**
 * Writes diagnostic output to some source.
 */
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
     * Writes some text that is part of the heading for some grouped output.
     */
    fun heading(text: String)

    /**
     * Writes the given text to the output, followed by line separator.
     */
    fun writeLine(text: String)

    /**
     * Writes the line separator.
     */
    fun writeLine()

    /**
     * Terminates the current line, if not already.
     */
    fun maybeNewLine()
}

/**
 * Returns the default [Output] for this process, which writes to the process' stderr.
 */
expect fun output(): Output
