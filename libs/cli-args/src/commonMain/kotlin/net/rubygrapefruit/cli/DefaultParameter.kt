package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultParameter<T : Any>(
    name: String,
    help: String?,
    host: Host,
    private val owner: Action,
    converter: StringConverter<T>
) : AbstractParameter<T>(name, help, host, converter), RequiredParameter<T> {

    override fun whenAbsent(default: T): Parameter<T> {
        return owner.replace(this, OptionalParameter(name, help, default, host, converter))
    }

    override fun optional(): Parameter<T?> {
        return owner.replace(this, NullableParameter(name, help, host, converter))
    }

    override fun repeated(): ListParameter<T> {
        return owner.replace(this, DefaultListParameter(name, help, host, owner, emptyList(), true, converter))
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException()
    }

    override fun usage(): PositionalUsage {
        return usage(Cardinality.Required)
    }

    override fun start(context: ParseContext): ParseState {
        return ParameterParseState(this, context, host, true, null, converter)
    }
}