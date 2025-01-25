package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class RemainderParameter(private val name: String, private val help: String?) : Positional, Parameter<List<String>> {
    private val value = mutableListOf<String>()

    override fun usage(name: String): ActionUsage? {
        return null
    }

    override fun usage(): PositionalUsage {
        return ParameterUsage("<$name>...", "<$name>", help, String::class, Cardinality.ZeroOrMore)
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        value.addAll(args)
        return ParseResult.Success(args.size)
    }

    override fun canAcceptMore(): Boolean {
        return true
    }

    override fun finished(context: ParseContext): FinishResult {
        return FinishResult.Success
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<String> {
        return value
    }
}