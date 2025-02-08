package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultListOption<T : Any>(
    private val matcher: Matcher<T>
) : ListOption<T>, Named {
    private val value = mutableListOf<T>()

    override val markers: List<String>
        get() = matcher.markers

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
        return value
    }

    override fun usage(): List<NonPositionalUsage> {
        return matcher.usage()
    }

    fun value(value: List<T>) {
        this.value.clear()
        this.value.addAll(value)
    }

    override fun start(context: ParseContext): ParseState {
        return ListOptionParseState(this, matcher)
    }
}