package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionSet<T: Action>(private val host: Host) : Positional(), Parameter<T>, Action.Actions<T> {
    private val actions = mutableMapOf<String, ActionDetails<T>>()
    private var action: T? = null

    private val actionInfo
        get() = actions.map { SubActionUsage(it.key, it.value.help) }

    override fun action(action: T, name: String, help: String?) {
        host.validate(name, "an action name")
        actions[name] = ActionDetails(action, help)
    }

    override fun accept(args: List<String>): ParseResult {
        val name = args.first()
        if (host.isOption(name)) {
            return ParseResult.Nothing
        }
        if (!actions.containsKey(name)) {
            return ParseResult(1, ArgParseException("Unknown action: $name", actions = actionInfo))
        }
        action = actions[name]!!.action
        val result = action!!.maybeParse(args.drop(1))
        return ParseResult(1 + result.count, result.failure)
    }

    override fun missing(): ArgParseException {
        return ArgParseException("Action not provided", resolution = "Please specify an action to run.", actions = actionInfo)
    }

    override fun usage(): PositionalUsage {
        return PositionalUsage("<action>", "<action>", null, actionInfo)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return action ?: throw IllegalStateException()
    }

    private class ActionDetails<T: Action>(val action: T, val help: String?)
}