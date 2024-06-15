package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultChoice<T>(private val host: Host) : NonPositional(), Option<T?>, Action.Choices<T> {
    private val choices = mutableMapOf<String, ChoiceDetails<T>>()
    private var value: T? = null

    override fun usage(): List<OptionUsage> {
        return choices.map { OptionUsage(it.key, it.value.help) }
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
        choices[host.option(name)] = details
        names.iterator().forEach { choices[host.option(it)] = details }
    }

    private class ChoiceDetails<T>(val value: T, val help: String?)
}