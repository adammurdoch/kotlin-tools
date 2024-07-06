package net.rubygrapefruit.cli.app

internal abstract class Formatter {
    private var atStartOfLine = true

    fun append(value: String) {
        if (value.isNotEmpty()) {
            text(value)
            atStartOfLine = value.endsWith('\n')
        }
    }

    fun maybeNewLine() {
        if (!atStartOfLine) {
            newLine()
        }
    }

    fun newLine() {
        text("\n")
        atStartOfLine = true
    }

    protected abstract fun text(value: String)
}

internal object LoggingFormatter : Formatter() {
    override fun text(value: String) {
        print(value)
    }
}