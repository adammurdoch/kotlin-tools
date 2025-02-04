package net.rubygrapefruit.cli

internal open class ChoiceFlagParseState<T : Any>(
    private val target: AbstractChoice<T>,
    private val matcher: ChoiceFlagMatcher<T>
) : ParseState {
    private var value: T? = null

    override fun parseNextValue(args: List<String>): ParseState.Result {
        val result = matcher.match(args)
        return when (result) {
            is Matcher.Success -> {
                value = result.value
                ParseState.Continue(1, this)
            }

            is Matcher.Nothing -> ParseState.Nothing

            is Matcher.Failure -> ParseState.Failure(1, result.message)
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