package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultListOption<T : Any>(
    private val matcher: OptionMatcher<T>
) : ListOption<T>, NonPositional {
    private val value = mutableListOf<T>()

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
        return value
    }

    override fun usage(): List<NonPositionalUsage> {
        TODO("Not yet implemented")
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val result = matcher.match(args)
        if (result is Matcher.Success) {
            value.add(result.value)
        }
        return result.asParseResult()
    }

    override fun accepts(option: String): Boolean {
        return matcher.accepts(option)
    }
}