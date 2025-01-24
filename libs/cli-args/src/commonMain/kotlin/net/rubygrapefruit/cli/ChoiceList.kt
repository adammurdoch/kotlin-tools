package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class ChoiceList<T : Any>(
    private val choices: List<ChoiceDetails<T>>
) : NonPositional, ListOption<T> {
    private val value = mutableListOf<T>()

    override fun usage(): List<NonPositionalUsage> {
        TODO("Not yet implemented")
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val name = args.first()
        val result = choices.firstOrNull { it.names.contains(name) }
        return if (result != null) {
            value.add(result.value)
            ParseResult.One
        } else {
            ParseResult.Nothing
        }
    }

    override fun accepts(option: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
        return value
    }
}