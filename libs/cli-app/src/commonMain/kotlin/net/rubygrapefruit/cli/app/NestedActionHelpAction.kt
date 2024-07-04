package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionParameterUsage

internal open class NestedActionHelpAction(
    private val name: String,
    private val action: Action
) : AbstractHelpAction() {
    private val actionName by parameter("action").optional()

    override val formatted: String
        get() {
            val builder = StringBuilder()
            if (actionName != null) {
                val positional = action.usage().effective().positional.firstOrNull()
                val nestedAction = if (positional is ActionParameterUsage) {
                    positional.named.find { it.name == actionName }
                } else {
                    null
                }
                if (nestedAction != null) {
                    builder.appendUsage(nestedAction.name, nestedAction.action)
                } else {
                    builder.append("Unknown action: $actionName")
                }
            } else {
                builder.appendUsage(name, action)
            }
            return builder.toString()
        }

}