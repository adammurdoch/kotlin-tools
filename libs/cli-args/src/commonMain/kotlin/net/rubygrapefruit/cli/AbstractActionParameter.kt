package net.rubygrapefruit.cli

internal abstract class AbstractActionParameter<T : Action>(
    protected val options: Map<String, ChoiceDetails<T>>,
    protected val parameters: Map<String, ChoiceDetails<T>>,
    protected val host: Host
) : Positional() {
    protected var action: T? = null

    protected val actionInfo
        get() = parameters.map { SubActionUsage(it.key, it.value.help, it.value.value.usage()) }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val name = args.first()
        val action = if (host.isOption(name)) {
            val option = options[name]
            if (option == null) {
                return ParseResult.Nothing
            }
            option.value
        } else {
            val parameter = parameters[name]
            if (parameter==null) {
                return ParseResult(1, ArgParseException("Unknown action: $name", actions = actionInfo), true)
            }
            parameter.value
        }
        this.action = action
        val result = action.maybeParse(args.drop(1), context, stopOnFailure = true)
        return ParseResult(1 + result.count, result.failure, result.finished)
    }

    override fun usage(): PositionalUsage {
        return ActionParameterUsage("<action>", "<action>", null, actionInfo)
    }
}
