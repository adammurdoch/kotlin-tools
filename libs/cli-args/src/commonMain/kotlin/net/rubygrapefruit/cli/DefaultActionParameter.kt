package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionParameter<T : Action>(
    private val actions: ActionSet<T>,
    private val host: Host
) : Positional, Parameter<T> {
    private var actionName: String? = null
    private var selected: T? = null

    val actionInfo
        get() = actions.named.map { NamedNestedActionUsage(it.key, it.value.help, it.value.value.usage()) }

    val recoverables: List<Recoverable> = actions.options.mapNotNull { if (it.value.allowAnywhere) AllowAnywhereOption(it.key, it.value) else null }

    val option: NonPositional = object : NonPositional {
        override fun usage(): List<NonPositionalUsage> {
            return emptyList()
        }

        override val inheritable: Boolean
            get() = false

        override fun accept(args: List<String>, context: ParseContext): ParseResult {
            val name = args.first()
            val action = actions.options[name]
            if (action == null) {
                return ParseResult.Nothing
            } else {
                if (selected != null) {
                    return ParseResult.Failure(0, ArgParseException("Cannot use option $name with $actionName"))
                }
                return parseAction("option $name", action, args, context)
            }
        }

        override fun accepts(option: String): Boolean {
            return actions.options.containsKey(option)
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return selected ?: throw IllegalStateException()
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val name = args.first()
        val action = actions.named[name]
        if (action != null) {
            if (selected != null) {
                return ParseResult.Failure(0, ArgParseException("Cannot use action '$name' with $actionName"))
            }
            val nestedContext = context.nested(this, listOf(NameUsage(name)) + action.value.positional())
            return parseAction("action '$name'", action, args, nestedContext)
        }
        if (this.selected != null) {
            return ParseResult.Nothing
        }
        if (actions.default != null) {
            this.selected = actions.default.value
            val nestedContext = context.nested(this, actions.default.value.positional())
            return actions.default.value.maybeParse(args, nestedContext)
        }

        return if (host.isOption(name)) {
            ParseResult.Nothing
        } else {
            ParseResult.Failure(1, PositionalParseException("Unknown action: $name", positional = context.positional, actions = actionInfo))
        }
    }

    private fun parseAction(
        name: String,
        action: ActionDetails<T>,
        args: List<String>,
        context: ParseContext
    ): ParseResult {
        this.actionName = name
        this.selected = action.value
        val result = action.value.maybeParse(args.drop(1), context)
        return result.prepend(1)
    }

    override fun canAcceptMore(): Boolean {
        return selected == null
    }

    override fun usage(): PositionalUsage {
        val optionInfo = actions.options.map { NamedNestedActionUsage(it.key, it.value.help, it.value.value.usage()) }
        val defaultInfo = actions.default?.let { DefaultNestedActionUsage(it.help, it.value.usage()) }
        return ActionParameterUsage("<action>", "<action>", null, optionInfo, actionInfo, defaultInfo)
    }

    override fun usage(name: String): ActionUsage? {
        val action = actions.named[name]
        if (action != null) {
            return action.value.usage()
        }
        if (actions.default != null) {
            return actions.default.value.usage(name)
        }

        return null
    }

    override fun finished(context: ParseContext): FinishResult {
        return when {
            selected != null -> FinishResult.Success
            actions.default != null -> {
                selected = actions.default.value
                val nestedContext = context.nested(this, actions.default.value.positional())
                actions.default.value.maybeParse(emptyList(), nestedContext).asFinishResult()
            }

            else -> {
                val exception =
                    PositionalParseException("Action not provided", resolution = "Please specify an action to run.", positional = context.positional, actions = actionInfo)
                FinishResult.Failure(exception, expectedMore = true)
            }
        }
    }

    fun start(context: ParseContext): ParseState {
        return ActionParameterParseState(this, context, actions, host)
    }

    class NameUsage(val name: String) : HasPositionalUsage {
        override fun usage(): PositionalUsage {
            return LiteralUsage(name, null)
        }
    }

    private inner class AllowAnywhereOption(val name: String, val option: ActionDetails<T>) : Recoverable {

        override fun toString(): String {
            return name
        }

        override fun maybeRecover(args: List<String>, context: ParseContext): Boolean {
            return if (selected == option.value) {
                true
            } else if (args.firstOrNull() == this.name) {
                selected = option.value
                val result = option.value.maybeParse(args.drop(1), context)
                result.failure == null
            } else {
                false
            }
        }
    }
}
