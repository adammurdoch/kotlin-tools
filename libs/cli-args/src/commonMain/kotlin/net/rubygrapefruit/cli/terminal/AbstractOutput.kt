package net.rubygrapefruit.cli.terminal

abstract class AbstractOutput : Output {
    private var emptyLine = true

    override fun write(text: String) {
        write(text, Appearance.Normal)
    }

    override fun userInput(text: String) {
        write(text, Appearance.UserInput)
    }

    override fun heading(text: String) {
        write(text, Appearance.Heading)
    }

    override fun writeLine(text: String) {
        put(text, Appearance.Normal)
        writeLine()
    }

    override fun writeLine() {
        putEndLine()
        emptyLine = true
    }

    override fun maybeNewLine() {
        if (!emptyLine) {
            writeLine()
        }
    }

    private fun write(text: String, appearance: Appearance) {
        put(text, appearance)
        emptyLine = text.endsWith("\n")
    }

    protected abstract fun put(text: CharSequence, appearance: Appearance)

    protected open fun putEndLine() {
        put("\n", Appearance.Normal)
    }

    protected enum class Appearance {
        Normal, UserInput, Heading
    }
}