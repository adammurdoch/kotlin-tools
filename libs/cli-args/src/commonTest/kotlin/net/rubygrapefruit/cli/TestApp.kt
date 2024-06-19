package net.rubygrapefruit.cli

open class TestApp(private val name: String) : Action() {
    val help by flag("help", help = "help message")

    override fun usage(): ActionUsage {
        val usage = super.usage()
        return ActionUsage(name, usage.options, usage.positional)
    }
}