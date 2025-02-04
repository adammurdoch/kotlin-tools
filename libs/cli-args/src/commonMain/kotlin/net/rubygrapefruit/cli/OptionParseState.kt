package net.rubygrapefruit.cli

internal open class OptionParseState<T : Any>(
    protected val target: AbstractOption<T>,
    private val matcher: OptionMatcher<T>
) : ParseState {
    private var value: T? = null

    override fun parseNextValue(args: List<String>): ParseState.Result {
        val result = matcher.match(args)
        if (result.consumed > 0 && value != null) {
            val arg = args.first()
            return ParseState.Failure(result.consumed, "Value for option $arg already provided")
        }
        return when (result) {
            is Matcher.Success -> {
                value = result.value
                ParseState.Continue(result.consumed, this)
            }

            is Matcher.Nothing -> ParseState.Nothing

            is Matcher.Failure -> ParseState.Failure(result.consumed, result.message)
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return if (value == null) {
            ParseState.FinishFailure("Option ${matcher.flags.maxBy { it.length }} not provided")
        } else {
            ParseState.FinishSuccess {
                target.value(value)
            }
        }
    }
}