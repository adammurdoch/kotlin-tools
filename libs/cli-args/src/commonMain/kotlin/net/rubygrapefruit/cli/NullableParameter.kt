package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class NullableParameter<T : Any>(
    name: String,
    help: String?,
    converter: StringConverter<T>
) : AbstractParameter<T>(name, help, converter), Parameter<T?> {

    override val usage: String
        get() = "<$name>?"

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }

    override fun usage(): PositionalUsage {
        return usage(Cardinality.Optional)
    }

    override fun start(context: ParseContext): ParseState {
        return ParameterParseState(this, context, false, null, converter)
    }
}