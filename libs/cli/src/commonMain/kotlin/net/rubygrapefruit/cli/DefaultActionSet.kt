package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionSet<T : Action>(
    private val actions: Map<String, ChoiceDetails<T>>,
    private val host: Host,
    private val owner: Action,
    private val default: T?
) : Positional(), Parameter<T> {
    private var action: T? = null

    private val actionInfo
        get() = actions.map { SubActionUsage(it.key, it.value.help) }

    override fun whenAbsent(default: T): Parameter<T> {
        val actions = DefaultActionSet(actions, host, owner, default)
        owner.replace(this, actions)
        return actions
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val name = args.first()
        if (host.isOption(name)) {
            return ParseResult.Nothing
        }
        if (!actions.containsKey(name)) {
            return ParseResult(1, ArgParseException("Unknown action: $name", actions = actionInfo), true)
        }
        action = actions.getValue(name).value
        val result = action!!.maybeParse(args.drop(1), context)
        return ParseResult(1 + result.count, result.failure, result.finished)
    }

    override fun missing(): ArgParseException? {
        return if (default == null) {
            ArgParseException("Action not provided", resolution = "Please specify an action to run.", actions = actionInfo)
        } else {
            null
        }
    }

    override fun usage(): PositionalUsage {
        return PositionalUsage("<action>", "<action>", null, actionInfo)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return (action ?: default) ?: throw IllegalStateException()
    }
}
