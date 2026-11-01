package net.rubygrapefruit.cli.terminal

abstract class AbstractOutput : Output {
    private var emptyLine = true

    override fun append(text: String) {
        put(text)
        emptyLine = text.endsWith("\n")
    }

    override fun appendLine(text: String) {
        put(text)
        put("\n")
        emptyLine = true
    }

    override fun maybeNewLine() {
        if (!emptyLine) {
            put("\n")
            emptyLine = true
        }
    }

    protected abstract fun put(text: CharSequence)
}