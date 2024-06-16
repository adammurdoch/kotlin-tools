package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultChoice<T : Any>(private val choices: Map<String, ChoiceDetails<T>>, private val default: T) : NonPositional(), Option<T> {
    private var value: T? = null

    override fun usage(): List<OptionUsage> {
        return choices.map { OptionUsage(it.key, it.value.help, listOf(SingleOptionUsage(it.key, it.value.help))) }
    }

    override fun accept(args: List<String>): ParseResult {
        val result = choices[args[0]]
        return if (result != null) {
            value = result.value
            ParseResult.One
        } else {
            ParseResult.Nothing
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: default
    }
}