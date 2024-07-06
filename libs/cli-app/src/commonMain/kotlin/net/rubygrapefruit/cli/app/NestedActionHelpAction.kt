package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionParameterUsage

internal open class NestedActionHelpAction(
    private val name: String,
    private val action: Action,
    private val formatter: Formatter
) : AbstractHelpAction() {
    private val actionName by parameter("action").optional()

    override fun run() {
        if (actionName != null) {
            val positional = action.usage().effective().positional.firstOrNull()
            val nestedAction = if (positional is ActionParameterUsage) {
                positional.named.find { it.name == actionName }
            } else {
                null
            }
            if (nestedAction != null) {
                formatter.appendUsage(nestedAction.name, nestedAction.action)
            } else {
                formatter.append("Unknown action: $actionName")
            }
        } else {
            formatter.appendUsage(name, action)
        }
    }
}