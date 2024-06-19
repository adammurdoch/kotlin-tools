package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultNullableChoice<T : Any>(
    private val choices: Map<String, ChoiceDetails<T>>,
    private val owner: Action
) : NonPositional(), NullableOption<T> {
    private var value: T? = null

    override fun usage(): List<OptionUsage> {
        return choices.map { OptionUsage(it.key, it.value.help, listOf(SingleOptionUsage(it.key, it.value.help, listOf(it.key)))) }
    }

    override fun whenAbsent(default: T): Option<T> {
        return owner.replace(this, DefaultChoice(choices, default))
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

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }
}
