package net.rubygrapefruit.cli.terminal

abstract class AbstractOutput : Output {
    private var emptyLine = true

    override fun write(text: String) {
        put(text, Appearance.None)
        emptyLine = text.endsWith("\n")
    }

    override fun userInput(text: String) {
        put(text, Appearance.UserInput)
        emptyLine = text.endsWith("\n")
    }

    override fun writeLine(text: String) {
        put(text, Appearance.None)
        putEndLine()
        emptyLine = true
    }

    override fun maybeNewLine() {
        if (!emptyLine) {
            putEndLine()
            emptyLine = true
        }
    }

    protected abstract fun put(text: CharSequence, appearance: Appearance)

    protected open fun putEndLine() {
        put("\n", Appearance.None)
    }

    protected enum class Appearance {
        None, UserInput
    }
}