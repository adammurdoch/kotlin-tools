package net.rubygrapefruit.cli

internal class OptionalParameterParseState<T : Any>(
    target: AbstractParameter<T>,
    context: ParseContext,
    host: Host,
    converter: StringConverter<T>,
    private val defaultValue: T?
) : ParameterParseState<T>(target, context, host, converter) {
    override fun endOfInput(): ParseState.FinishResult {
        return ParseState.FinishSuccess {
            target.value(defaultValue)
        }
    }
}