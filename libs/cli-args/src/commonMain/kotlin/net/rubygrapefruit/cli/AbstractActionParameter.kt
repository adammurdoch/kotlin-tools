package net.rubygrapefruit.cli

internal abstract class AbstractActionParameter<T : Action>(
    protected val actions: ActionSet<T>,
    protected val host: Host
) : Positional {
    private var actionName: String? = null
    protected var action: T? = null

    protected val actionInfo
        get() = actions.named.map { NamedNestedActionUsage(it.key, it.value.help, it.value.value.usage()) }

    val recoverables: List<Recoverable> = actions.options.mapNotNull { if (it.value.allowAnywhere) AllowAnywhereOption(it.key, it.value) else null }

    val option: NonPositional = object : NonPositional {
        override fun usage(): List<NonPositionalUsage> {
            return emptyList()
        }

        override fun accept(args: List<String>, context: ParseContext): ParseResult {
            val name = args.first()
            val action = actions.options[name]
            if (action == null) {
                return ParseResult.Nothing
            } else {
                return parseAction(name, action, args, context)
            }
        }

        override fun accepts(option: String): Boolean {
            return actions.options.containsKey(option)
        }
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        if (this.action != null) {
            return ParseResult.Nothing
        }

        val name = args.first()
        val action = actions.named[name]
        if (action != null) {
            val nestedContext = context.replace(this, listOf(NameUsage(name)) + action.value.positional())
            return parseAction(name, action, args, nestedContext)
        }
        if (actions.default != null) {
            this.action = actions.default.value
            val nestedContext = context.replace(this, actions.default.value.positional())
            return actions.default.value.maybeParse(args, nestedContext)
        }

        if (host.isOption(name)) {
            return ParseResult.Nothing
        } else {
            return ParseResult.Failure(1, PositionalParseException("Unknown action: $name", positional = context.positional, actions = actionInfo))
        }
    }

    private fun parseAction(
        name: String,
        action: ActionDetails<T>,
        args: List<String>,
        context: ParseContext
    ): ParseResult {
        this.actionName = name
        this.action = action.value
        val result = action.value.maybeParse(args.drop(1), context)
        return result.prepend(1)
    }

    override fun canAcceptMore(): Boolean {
        return action == null
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
            action != null -> FinishResult.Success
            actions.default != null -> {
                action = actions.default.value
                val nestedContext = context.replace(this, actions.default.value.positional())
                return actions.default.value.maybeParse(emptyList(), nestedContext).asFinishResult()
            }

            else -> return whenMissing(context)
        }
    }

    abstract fun whenMissing(context: ParseContext): FinishResult

    private class NameUsage(val name: String) : HasPositionalUsage {
        override fun usage(): PositionalUsage {
            return LiteralUsage(name, null)
        }
    }

    private inner class AllowAnywhereOption(val name: String, val option: ActionDetails<T>) : Recoverable {

        override fun toString(): String {
            return name
        }

        override fun maybeRecover(args: List<String>, context: ParseContext): Boolean {
            return if (action == option.value) {
                true
            } else if (args.firstOrNull() == this.name) {
                action = option.value
                val result = option.value.maybeParse(args.drop(1), context)
                result.failure == null
            } else {
                false
            }
        }
    }
}
