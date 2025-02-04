package net.rubygrapefruit.cli

internal open class ParameterParseState<T : Any>(
    protected val target: AbstractParameter<T>,
    private val host: Host,
    private val converter: StringConverter<T>
) : ParseState {
    override fun parseNextValue(args: List<String>, context: ParseContext): ParseState.Result {
        val candidate = args.first()
        return if (host.isOption(candidate)) {
            ParseState.Nothing
        } else {
            val result = converter.convert("parameter '${target.name}'", candidate)
            when (result) {
                is StringConverter.Success -> ParseState.Success(1) {
                    target.value(result.value)
                }

                is StringConverter.Failure -> ParseState.Failure(1, result.message)
            }
        }
    }

    override fun endOfInput(context: ParseContext): ParseState.FinishResult {
        return ParseState.FinishFailure(
            "Parameter '${target.name}' not provided",
            resolution = "Please provide a value for parameter '${target.name}'.",
            positional = context.positional
        )
    }
}