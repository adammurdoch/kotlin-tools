package net.rubygrapefruit.cli

internal abstract class AbstractActionParameter<T : Action>(
    protected val actions: ActionSet<T>,
    protected val host: Host
) : Positional {
    private var actionName: String? = null
    protected var action: T? = null

    protected val actionInfo
        get() = actions.named.map { NamedNestedActionUsage(it.key, it.value.help, it.value.value.usage()) }

    val nonPositional: List<NonPositional> = actions.options.map { if (it.value.allowAnywhere) AllowAnywhereOption(it.key, it.value) else DisallowMultipleUseNonPositional(it.key) }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        if (this.action != null) {
            return ParseResult.Nothing
        }

        val name = args.first()
        val action = locateActionByFirstArg(name)
        if (action != null) {
            this.action = action.value
            this.actionName = name
            val nestedContext = context.replace(this, listOf(NameUsage(name)) + action.value.positional())
            val result = action.value.maybeParse(args.drop(1), nestedContext)
            return ParseResult(1 + result.count, result.failure)
        }
        if (actions.default != null) {
            this.action = actions.default.value
            val nestedContext = context.replace(this, actions.default.value.positional())
            return actions.default.value.maybeParse(args, nestedContext)
        }

        if (host.isOption(name)) {
            return ParseResult.Nothing
        } else {
            return ParseResult(1, PositionalParseException("Unknown action: $name", positional = context.positional, actions = actionInfo))
        }
    }

    private fun locateActionByFirstArg(name: String?): ActionDetails<T>? {
        if (name == null) {
            return null
        }
        if (host.isOption(name)) {
            return actions.options[name]
        }
        return actions.named[name]
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

    override fun finished(context: ParseContext): ArgParseException? {
        return when {
            action != null -> null
            actions.default != null -> {
                action = actions.default.value
                val nestedContext = context.replace(this, actions.default.value.positional())
                return actions.default.value.maybeParse(emptyList(), nestedContext).failure
            }

            else -> return whenMissing(context)
        }
    }

    abstract fun whenMissing(context: ParseContext): ArgParseException?

    private class NameUsage(val name: String) : HasPositionalUsage {
        override fun usage(): PositionalUsage {
            return LiteralUsage(name, null)
        }
    }

    private inner class DisallowMultipleUseNonPositional(val name: String) : NonPositional {
        override fun usage(): List<NonPositionalUsage> {
            return emptyList()
        }

        override fun accept(args: List<String>, context: ParseContext): ParseResult {
            return ParseResult.Nothing
        }

        override fun stoppedAt(arg: String): NonPositional.StopResult {
            return if (arg == name && action != null && actionName != null) {
                NonPositional.StopResult.Failure(ArgParseException("Cannot use $name with $actionName."))
            } else {
                NonPositional.StopResult.Nothing
            }
        }
    }

    private inner class AllowAnywhereOption(val name: String, val option: ActionDetails<T>) : NonPositional {

        override fun toString(): String {
            return name
        }

        override fun usage(): List<OptionUsage> {
            return emptyList()
        }

        override fun stoppedAt(arg: String): NonPositional.StopResult {
            return if (arg == name) NonPositional.StopResult.Recognized else NonPositional.StopResult.Nothing
        }

        override fun accept(args: List<String>, context: ParseContext): ParseResult {
            return ParseResult.Nothing
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
