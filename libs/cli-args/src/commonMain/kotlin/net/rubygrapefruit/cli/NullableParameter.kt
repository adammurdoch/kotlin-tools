package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class NullableParameter<T : Any>(
    name: String,
    help: String?,
    host: Host,
    converter: StringConverter<T>
) : AbstractParameter<T>(name, help, true, host, converter), Parameter<T?> {

    override val usage: String
        get() = "<$name>?"

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }

    override fun usage(): PositionalUsage {
        return usage(Cardinality.Optional)
    }

    override fun finished(context: ParseContext): FinishResult {
        return FinishResult.Success
    }

    fun start(context: ParseContext): ParseState {
        return OptionalParameterParseState(this, context, host, converter, null)
    }
}