package net.rubygrapefruit.cli

internal class ListParameterParseState<T : Any>(
    private val target: DefaultListParameter<T>,
    private val default: List<T>,
    private val required: Boolean,
    private val host: Host,
    private val converter: StringConverter<T>
) : ParseState {
    private val values = mutableListOf<T>()

    override fun parseNextValue(args: List<String>, context: ParseContext): ParseState.Result {
        val candidate = args.first()
        return if (host.isOption(candidate)) {
            ParseState.Nothing
        } else {
            val result = converter.convert("parameter '${target.name}'", candidate)
            when (result) {
                is StringConverter.Success -> {
                    values.add(result.value)
                    ParseState.Continue(1, this)
                }

                is StringConverter.Failure -> ParseState.Failure(1, result.message)
            }
        }
    }

    override fun endOfInput(context: ParseContext): ParseState.FinishResult {
        return if (required && values.isEmpty()) {
            ParseState.FinishFailure("Parameter '${target.name}' not provided")
        } else {
            val result = if (values.isNotEmpty()) values else default
            ParseState.FinishSuccess {
                target.values(result)
            }
        }
    }
}