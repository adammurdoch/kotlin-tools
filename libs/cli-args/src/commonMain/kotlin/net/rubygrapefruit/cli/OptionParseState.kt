package net.rubygrapefruit.cli

internal open class OptionParseState<T : Any>(
    protected val target: AbstractOption<T>,
    private val matcher: OptionMatcher<T>
) : ParseState, FailureHint {
    override fun parseNextValue(args: List<String>): ParseState.Result {
        val result = matcher.match(args)
        return when (result) {
            is Matcher.Success -> {
                ParseState.Success(result.consumed, this) {
                    target.value(result.value)
                }
            }

            is Matcher.Nothing -> ParseState.Nothing

            is Matcher.Failure -> result.toParseState()
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return ParseState.FinishFailure("Option ${matcher.flags.maxBy { it.length }} not provided")
    }

    override fun map(args: List<String>): ParseState.Failure? {
        val result = matcher.match(args)
        return if (result.consumed > 0) {
            val arg = args.first()
            ParseState.Failure(result.consumed, "Value for option $arg already provided")
        } else {
            null
        }
    }
}