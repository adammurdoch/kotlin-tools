package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class OptionalParameter<T : Any>(
    name: String,
    help: String?,
    private val default: T,
    host: Host,
    converter: StringConverter<T>
) : AbstractParameter<T>(name, help, true, host, converter), Parameter<T> {

    override val usage: String
        get() = "<$name>?"

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: default
    }

    override fun usage(): PositionalUsage {
        return usage(Cardinality.Optional)
    }

    override fun finished(context: ParseContext): FinishResult {
        return FinishResult.Success
    }

    override fun start(context: ParseContext): ParseState {
        return OptionalParameterParseState(this, context, host, converter, default)
    }
}