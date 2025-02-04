package net.rubygrapefruit.cli

internal class OptionalChoiceFlagParseState<T : Any>(
    target: AbstractChoiceFlag<T>,
    matcher: ChoiceFlagMatcher<T>,
    private val defaultValue: T?
) : ChoiceFlagParseState<T>(target, matcher) {
    override fun endOfInput(): ParseState.FinishResult {
        return ParseState.FinishSuccess {
            target.value(defaultValue)
        }
    }
}