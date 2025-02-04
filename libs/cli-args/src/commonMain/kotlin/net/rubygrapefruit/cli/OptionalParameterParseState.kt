package net.rubygrapefruit.cli

internal class OptionalParameterParseState<T : Any>(
    target: AbstractParameter<T>,
    host: Host,
    converter: StringConverter<T>,
    private val defaultValue: T?
) : ParameterParseState<T>(target, host, converter) {
    override fun endOfInput(context: ParseContext): ParseState.FinishResult {
        return ParseState.FinishSuccess {
            target.value(defaultValue)
        }
    }
}