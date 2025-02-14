package net.rubygrapefruit.cli

internal class ListParameterParseState<T : Any>(
    private val target: DefaultListParameter<T>,
    private val context: ParseContext,
    private val default: List<T>,
    private val required: Boolean,
    private val converter: StringConverter<T>
) : ParseState {
    private val values = mutableListOf<T>()

    override fun parseNextValue(args: List<String>): ParseState.Result {
        val candidate = args.first()
        return if (context.isOption(candidate)) {
            ParseState.Nothing
        } else {
            val result = converter.convert("parameter '${target.name}'", candidate)
            when (result) {
                is StringConverter.Success -> {
                    values.add(result.value)
                    ParseState.Continue(1, this, null) {}
                }

                is StringConverter.Failure -> ParseState.Failure(1, result.message)
            }
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return if (required && values.isEmpty()) {
            missingParameter(target.name, context)
        } else {
            val result = if (values.isNotEmpty()) values else default
            ParseState.FinishSuccess {
                target.values(result)
            }
        }
    }
}