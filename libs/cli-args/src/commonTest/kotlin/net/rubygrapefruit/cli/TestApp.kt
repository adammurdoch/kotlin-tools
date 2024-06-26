package net.rubygrapefruit.cli

open class TestApp : Action() {
    val help by flag("help", help = "help message")

    override fun usage(): ActionUsage {
        val usage = super.usage()
        return ActionUsage(usage.options, usage.positional)
    }
}