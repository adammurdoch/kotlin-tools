package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action

internal open class HelpAction(
    private val name: String,
    private val action: Action
) : AbstractHelpAction() {

    override val formatted: String
        get() {
            val builder = StringBuilder()
            builder.appendUsage(name, action)
            return builder.toString()
        }
}