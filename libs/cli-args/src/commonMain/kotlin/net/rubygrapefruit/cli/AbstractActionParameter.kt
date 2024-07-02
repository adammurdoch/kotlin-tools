package net.rubygrapefruit.cli

internal abstract class AbstractActionParameter<T : Action>(
    protected val actions: ActionSet<T>,
    protected val host: Host
) : Positional() {
    protected var action: T? = null

    protected val actionInfo
        get() = actions.named.map { NamedNestedActionUsage(it.key, it.value.help, it.value.value.usage()) }

    val nonPositional: List<NonPositional> = actions.options.filter { it.value.allowAnywhere }.map { AllowAnywhereOption(it.key, it.value) }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        if (this.action != null) {
            return ParseResult.Nothing
        }

        val name = args.firstOrNull()
        val action = locateActionByFirstArg(name)
        if (action != null) {
            this.action = action.value
            val result = action.value.maybeParse(args.drop(1), context)
            return ParseResult(1 + result.count, result.failure)
        }
        if (actions.default != null) {
            this.action = actions.default.value
            return actions.default.value.maybeParse(args, context)
        }

        if (name == null || host.isOption(name)) {
            return ParseResult.Nothing
        } else {
            return ParseResult(1, ArgParseException("Unknown action: $name", actions = actionInfo))
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

    override fun finished(): ArgParseException? {
        return when {
            action != null -> null
            actions.default != null -> {
                action = actions.default.value
                return actions.default.value.maybeParse(emptyList(), RootContext).failure
            }

            else -> return whenMissing()
        }
    }

    abstract fun whenMissing(): ArgParseException?

    private inner class AllowAnywhereOption(val name: String, val option: ActionDetails<T>) : NonPositional {

        override fun toString(): String {
            return name
        }

        override fun usage(): List<OptionUsage> {
            return emptyList()
        }

        override fun accepts(arg: String): Boolean {
            return arg == name
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
