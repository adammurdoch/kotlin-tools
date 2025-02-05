package net.rubygrapefruit.cli

internal class ListOptionParseState<T : Any>(
    private val target: DefaultListOption<T>,
    private val matcher: Matcher<T>
) : ParseState {
    private val values = mutableListOf<T>()

    override fun parseNextValue(args: List<String>): ParseState.Result {
        val result = matcher.match(args)
        return when (result) {
            is Matcher.Success -> {
                values.add(result.value)
                ParseState.Continue(result.consumed, this, null) {}
            }

            is Matcher.Nothing -> ParseState.Nothing

            is Matcher.Failure -> result.toParseState()
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return ParseState.FinishSuccess {
            target.value(values)
        }
    }
}