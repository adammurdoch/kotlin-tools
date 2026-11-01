package net.rubygrapefruit.cli.terminal

class BufferingOutput : AbstractOutput() {
    private val builder = StringBuilder()

    val text: String
        get() = builder.toString()

    override fun put(text: CharSequence) {
        builder.append(text)
    }
}