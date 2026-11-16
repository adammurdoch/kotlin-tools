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

    /**
     * Writes a list of definitions (term + description) to the output
     */
    fun definitionList(builder: Definitions.() -> Unit)

    interface Definitions {
        /**
         * Adds an item to the list
         */
        fun item(builder: Definition.() -> Unit)
    }

    interface Definition {
        fun term(builder: Output.() -> Unit)

        fun description(builder: Output.() -> Unit)
    }
}

/**
 * Returns the default [Output] for this process, which writes to the process' stdout.
 */
expect fun output(): Output

/**
 * Returns the default diagnostic [Output] for this process, which writes to the process' stderr.
 */
expect fun diagnostics(): Output
