package net.rubygrapefruit.cli

internal class HelpAction(val action: MainAction) : Action() {
    override fun run() {
        val usage = action.usage()
        for (line in usage.formatted.lines()) {
            println(line)
        }
    }
}