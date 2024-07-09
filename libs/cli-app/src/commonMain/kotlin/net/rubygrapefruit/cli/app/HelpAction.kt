package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action

internal open class HelpAction(
    private val name: String,
    private val action: Action,
    private val formatter: Formatter
) : Action() {

    override fun run() {
        formatter.appendUsage(name, action)
    }
}