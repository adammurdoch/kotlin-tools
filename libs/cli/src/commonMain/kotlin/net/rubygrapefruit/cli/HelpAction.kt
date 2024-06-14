package net.rubygrapefruit.cli

internal class HelpAction(val action: Action) : Action() {
    override fun run() {
        val usage = action.usage()
        print("Usage: [options]")
        for (positional in usage.positional) {
            print(" ${positional.usage}")
        }
        println()
        println()
        if (usage.options.isNotEmpty()) {
            println("Options:")
            for (option in usage.options) {
                println("  ${option.usage}")
            }
        }
    }
}