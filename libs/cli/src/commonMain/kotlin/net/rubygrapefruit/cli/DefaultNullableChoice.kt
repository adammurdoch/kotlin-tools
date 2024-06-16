package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultNullableChoice<T : Any>(private val host: Host, private val owner: Action) : NonPositional(), NullableOption<T>, Action.Choices<T> {
    private val choices = mutableMapOf<String, ChoiceDetails<T>>()
    private var value: T? = null

    override fun usage(): List<OptionUsage> {
        return choices.map { OptionUsage(it.key, it.value.help, listOf(SingleOptionUsage(it.key, it.value.help))) }
    }

    override fun default(value: T): Option<T> {
        val choice = DefaultChoice(choices, value)
        owner.replace(this, choice)
        return choice
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

    override fun choice(value: T, name: String, vararg names: String, help: String?) {
        val details = ChoiceDetails(value, help)
        val allNames = listOf(name) + names
        allNames.iterator().forEach {
            host.validate(it, "an option name")
            choices[host.option(it)] = details
        }
    }
}

class ChoiceDetails<T>(val value: T, val help: String?)
