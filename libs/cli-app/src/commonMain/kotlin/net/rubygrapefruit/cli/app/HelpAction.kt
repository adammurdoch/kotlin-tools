package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action

internal class HelpAction(val action: CliApp) : Action() {
    override fun run() {
        val usage = action.usage()
        for (line in usage.formatted.lines()) {
            println(line)
        }
    }
}