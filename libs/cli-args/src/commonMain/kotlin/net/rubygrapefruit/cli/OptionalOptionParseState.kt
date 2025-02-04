package net.rubygrapefruit.cli

internal class OptionalOptionParseState<T : Any>(
    target: AbstractOption<T>,
    matcher: OptionMatcher<T>,
    private val defaultValue: T?
) : OptionParseState<T>(target, matcher) {
    override fun endOfInput(): ParseState.FinishResult {
        return ParseState.FinishSuccess {
            target.value(defaultValue)
        }
    }
}