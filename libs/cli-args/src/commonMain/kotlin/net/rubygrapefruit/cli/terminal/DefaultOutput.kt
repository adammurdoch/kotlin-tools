package net.rubygrapefruit.cli.terminal

object DefaultOutput : AbstractOutput() {
    override fun put(text: CharSequence) {
        print(text)
    }
}