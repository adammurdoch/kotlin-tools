package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultListParameter<T : Any>(
    val name: String,
    private val help: String?,
    private val owner: Action,
    private val default: List<T>,
    private val required: Boolean,
    private val converter: StringConverter<T>
) : Positional, ListParameter<T> {
    private val values = mutableListOf<T>()

    override fun whenAbsent(default: List<T>): Parameter<List<T>> {
        return owner.replace(this, DefaultListParameter(name, help, owner, default, false, converter))
    }

    override fun optional(): Parameter<List<T>> {
        return if (!required) {
            this
        } else {
            owner.replace(this, DefaultListParameter(name, help, owner, emptyList(), false, converter))
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
        return if (values.isNotEmpty()) values.toList() else default
    }

    override fun usage(): PositionalUsage {
        return ParameterUsage("<$name>...", "<$name>", help, converter.type, if (required) Cardinality.OneOrMore else Cardinality.ZeroOrMore, converter.candidateValues)
    }

    override fun usage(name: String): ActionUsage? {
        return null
    }

    override fun start(context: ParseContext): ParseState {
        return ListParameterParseState(this, context, default, required, converter)
    }

    fun values(values: List<T>) {
        this.values.clear()
        this.values.addAll(values)
    }
}