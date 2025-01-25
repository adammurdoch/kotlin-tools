package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class RemainderParameter(
    private val name: String,
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

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        values.addAll(args)
        return ParseResult.Success(args.size)
    }

    override fun canAcceptMore(): Boolean {
        return true
    }

    override fun finished(context: ParseContext): FinishResult {
        return if (required && values.isEmpty()) {
            FinishResult.Failure(ArgParseException("Parameter '$name' not provided"), expectedMore = true)
        } else {
            FinishResult.Success
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<String> {
        return values
    }
}