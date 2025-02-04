package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultOption<T : Any>(
    matcher: OptionMatcher<T>,
    val default: T
) : AbstractOption<T>(matcher), Option<T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: default
    }

    override fun start(context: ParseContext): ParseState {
        return OptionalOptionParseState(this, matcher, default)
    }
}