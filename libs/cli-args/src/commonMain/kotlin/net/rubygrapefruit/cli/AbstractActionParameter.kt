package net.rubygrapefruit.cli

internal abstract class AbstractActionParameter<T : Action>(
    protected val actions: ActionSet<T>,
    protected val host: Host
) : Positional() {
    protected var action: T? = null

    protected val actionInfo
        get() = actions.named.map { NamedNestedActionUsage(it.key, it.value.help, it.value.value.usage()) }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val name = args.firstOrNull()
        val action = locateActionByFirstArg(name)
        if (action != null) {
            this.action = action.value
            val result = action.value.maybeParse(args.drop(1), context, stopOnFailure = true)
            return ParseResult(1 + result.count, result.failure, result.finished)
        }
        if (actions.default != null) {
            this.action = actions.default.value
            val result = actions.default.value.maybeParse(args, context, stopOnFailure = true)
            return result
        }

        if (name == null || host.isOption(name)) {
            return ParseResult.Nothing
        } else {
            return ParseResult(1, ArgParseException("Unknown action: $name", actions = actionInfo), true)
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

    override fun usage(): PositionalUsage {
        val optionInfo = actions.options.map { NamedNestedActionUsage(it.key, it.value.help, it.value.value.usage()) }
        val defaultInfo = actions.default?.let { DefaultNestedActionUsage(it.help, it.value.usage()) }
        return ActionParameterUsage("<action>", "<action>", null, optionInfo, actionInfo, defaultInfo)
    }

    override fun finished(): ArgParseException? {
        if (actions.default != null) {
            this.action = actions.default.value
            return actions.default.value.maybeParse(emptyList(), RootContext, stopOnFailure = true).failure
        } else {
            return whenMissing()
        }
    }

    abstract fun whenMissing(): ArgParseException?
}
