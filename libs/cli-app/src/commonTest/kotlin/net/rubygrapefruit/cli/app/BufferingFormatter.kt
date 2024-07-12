package net.rubygrapefruit.cli.app

internal class BufferingFormatter : Formatter() {
    private val buffer = StringBuilder()

    val text: String
        get() = buffer.toString()

    override fun write(value: String) {
        buffer.append(value)
    }
}