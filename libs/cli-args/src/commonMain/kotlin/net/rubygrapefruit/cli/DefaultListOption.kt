package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultListOption<T : Any>(
    names: List<String>,
    private val host: Host,
    private val converter: StringConverter<T>
) : ListOption<T>, NonPositional {
    private val flags = names.map { host.option(it) }
    private val value = mutableListOf<T>()

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
        return value
    }

    override fun usage(): List<NonPositionalUsage> {
        TODO("Not yet implemented")
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val arg = args.first()
        if (!flags.contains(arg)) {
            return ParseResult.Nothing
        }
        val result = converter.convert("option $arg", args[1])
        value.add(result.getOrThrow())
        return ParseResult.Two
    }

    override fun accepts(option: String): Boolean {
        TODO("Not yet implemented")
    }
}