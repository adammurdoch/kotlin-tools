package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class RemainderParameter(
    val name: String,
    private val help: String?,
    private val required: Boolean,
    private val owner: Action
) : Positional, OptionalListParameter<String> {
    private val values = mutableListOf<String>()

    override fun usage(name: String): ActionUsage? {
        return null
    }

    override fun usage(): PositionalUsage {
        return ParameterUsage("<$name>...", "<$name>", help, String::class, Cardinality.ZeroOrMore)
    }

    override fun required(): Parameter<List<String>> {
        return if (required) this else owner.replace(this, RemainderParameter(name, help, true, owner))
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<String> {
        return values
    }

    fun values(values: List<String>) {
        this.values.clear()
        this.values.addAll(values)
    }

    override fun start(context: ParseContext): ParseState {
        return RemainderParseState(this, context, required)
    }
}