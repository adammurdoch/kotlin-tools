package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class ChoiceList<T : Any>(
    private val choices: ChoiceFlagMatcher<T>
) : NonPositional, ListOption<T> {
    private val value = mutableListOf<T>()

    override fun usage(): List<NonPositionalUsage> {
        TODO("Not yet implemented")
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val result = choices.match(args)
        if (result is Matcher.Success) {
            value.add(result.value)
        }
        return result.asParseResult()
    }

    override fun accepts(option: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
        return value
    }
}