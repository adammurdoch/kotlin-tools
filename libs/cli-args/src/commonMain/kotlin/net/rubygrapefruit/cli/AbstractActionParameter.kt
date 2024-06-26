package net.rubygrapefruit.cli

internal abstract class AbstractActionParameter<T : Action>(
    protected val actions: ActionSet<T>,
    protected val host: Host
) : Positional() {
    protected var action: T? = null

    protected val actionInfo
        get() = actions.named.map { SubActionUsage(it.key, it.value.help, it.value.value.usage()) }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val name = args.first()
        val action = if (host.isOption(name)) {
            val option = actions.options[name]
            if (option == null) {
                return ParseResult.Nothing
            }
            option.value
        } else if (actions.named.containsKey(name)) {
            val parameter = actions.named.getValue(name)
            parameter.value
        } else if (actions.default != null) {
            this.action = actions.default.value
            val result = actions.default.value.maybeParse(args, context, stopOnFailure = true)
            return result
        } else {
            return ParseResult(1, ArgParseException("Unknown action: $name", actions = actionInfo), true)
        }
        this.action = action
        val result = action.maybeParse(args.drop(1), context, stopOnFailure = true)
        return ParseResult(1 + result.count, result.failure, result.finished)
    }

    override fun usage(): PositionalUsage {
        return ActionParameterUsage("<action>", "<action>", null, actionInfo)
    }

    override fun missing(): ArgParseException? {
        if (actions.default != null) {
            this.action = actions.default.value
            return actions.default.value.maybeParse(emptyList(), RootContext, stopOnFailure = true).failure
        } else {
            return whenMissing()
        }
    }

    abstract fun whenMissing(): ArgParseException?
}
