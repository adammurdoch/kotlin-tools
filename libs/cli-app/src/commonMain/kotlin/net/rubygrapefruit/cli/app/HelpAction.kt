package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action

internal open class HelpAction(
    private val name: String,
    private val action: Action,
    private val formatter: Formatter
) : AbstractHelpAction() {

    override fun run() {
        run(formatter)
    }

    internal fun run(formatter: Formatter) {
        formatter.appendUsage(name, action)
    }
}