package net.rubygrapefruit.cli.terminal

abstract class AbstractPlainOutput : AbstractOutput() {
    override fun put(text: CharSequence, appearance: Appearance) {
        when (appearance) {
            Appearance.Normal, Appearance.Heading -> put(text)
            Appearance.UserInput -> {
                put("'")
                put(text)
                put("'")
            }
        }
    }

    protected abstract fun put(text: CharSequence)
}