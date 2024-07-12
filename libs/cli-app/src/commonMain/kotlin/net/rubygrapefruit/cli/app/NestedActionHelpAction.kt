package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action

internal open class NestedActionHelpAction(
    private val name: String,
    private val action: MainAction,
    private val formatter: Formatter
) : Action() {
    private val actionName by parameter("action").optional()

    override fun run() {
        val name = actionName
        if (name != null) {
            val nestedAction = action.usage(name)
            if (nestedAction != null) {
                formatter.appendUsage(this.name, nestedAction)
            } else {
                throw RuntimeException("Unknown action: $name")
            }
        } else {
            formatter.appendUsage(this.name, action)
        }
    }
}