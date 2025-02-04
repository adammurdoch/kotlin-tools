package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class OptionalChoiceFlag<T : Any>(
    choices: ChoiceFlagMatcher<T>,
    private val default: T
) : AbstractChoiceFlag<T>(choices), Option<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: default
    }

    fun start(context: ParseContext): ParseState {
        return OptionalChoiceFlagParseState(this, matcher, default)
    }
}