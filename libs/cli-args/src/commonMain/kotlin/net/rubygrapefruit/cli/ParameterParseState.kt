package net.rubygrapefruit.cli

internal class ParameterParseState<T : Any>(
    private val target: AbstractParameter<T>,
    private val context: ParseContext,
    private val host: Host,
    private val required: Boolean,
    private val defaultValue: T?,
    private val converter: StringConverter<T>
) : ParseState {
    override fun parseNextValue(args: List<String>): ParseState.Result {
        val candidate = args.first()
        return if (host.isOption(candidate)) {
            ParseState.Nothing
        } else {
            val result = converter.convert("parameter '${target.name}'", candidate)
            when (result) {
                is StringConverter.Success -> {
                    ParseState.Success(1) {
                        target.value(result.value)
                    }
                }

                is StringConverter.Failure -> {
                    if (required) {
                        ParseState.Failure(1, result.message)
                    } else {
                        ParseState.Success(0) {
                            target.value(defaultValue)
                        }
                    }
                }
            }
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return if (required) {
            ParseState.FinishFailure(
                "Parameter '${target.name}' not provided",
                resolution = "Please provide a value for parameter '${target.name}'.",
                positional = context.positional
            )
        } else {
            ParseState.FinishSuccess {
                target.value(defaultValue)
            }
        }
    }
}