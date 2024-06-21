package net.rubygrapefruit.cli

internal abstract class AbstractActionParameter<T : Action>(
    protected val actions: Map<String, ChoiceDetails<T>>,
    protected val host: Host
) : Positional() {
    protected var action: T? = null

    protected val actionInfo
        get() = actions.map { SubActionUsage(it.key, it.value.help, it.value.value.usage()) }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val name = args.first()
        if (host.isOption(name)) {
            return ParseResult.Nothing
        }
        if (!actions.containsKey(name)) {
            return ParseResult(1, ArgParseException("Unknown action: $name", actions = actionInfo), true)
        }
        action = actions.getValue(name).value
        val result = action!!.maybeParse(args.drop(1), context, stopOnFailure = true)
        return ParseResult(1 + result.count, result.failure, result.finished)
    }

    override fun usage(): PositionalUsage {
        return ActionParameterUsage("<action>", "<action>", null, actionInfo)
    }
}
