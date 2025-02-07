package net.rubygrapefruit.cli

internal class ParameterParseState<T : Any>(
    private val target: AbstractParameter<T>,
    private val context: ParseContext,
    private val required: Boolean,
    private val defaultValue: T?,
    private val converter: StringConverter<T>
) : ParseState {
    override fun parseNextValue(args: List<String>): ParseState.Result {
        val candidate = args.first()
        return if (context.isOption(candidate)) {
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
            missingParameter(target.name, context)
        } else {
            ParseState.FinishSuccess {
                target.value(defaultValue)
            }
        }
    }
}