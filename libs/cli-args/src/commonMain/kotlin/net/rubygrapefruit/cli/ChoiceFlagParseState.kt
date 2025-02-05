package net.rubygrapefruit.cli

internal open class ChoiceFlagParseState<T : Any>(
    protected val target: AbstractChoiceFlag<T>,
    private val matcher: ChoiceFlagMatcher<T>
) : ParseState {
    protected var value: T? = null
        private set

    override fun parseNextValue(args: List<String>): ParseState.Result {
        val result = matcher.match(args)
        return when (result) {
            is Matcher.Success -> {
                value = result.value
                // Keep going to allow the value to be overridden by a later flag
                ParseState.Continue(result.consumed, this)
            }

            is Matcher.Nothing -> ParseState.Nothing

            is Matcher.Failure -> result.toParseState()
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return if (value == null) {
            ParseState.FinishFailure("One of the following options must be provided: ${matcher.choices.joinToString { it.names.maxBy { it.length } }}")
        } else {
            ParseState.FinishSuccess {
                target.value(value)
            }
        }
    }
}