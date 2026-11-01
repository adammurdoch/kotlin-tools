package net.rubygrapefruit.cli.terminal

class BufferingOutput : AbstractPlainOutput() {
    private val builder = StringBuilder()

    val text: String
        get() = builder.toString()

    override fun put(text: CharSequence) {
        builder.append(text)
    }
}