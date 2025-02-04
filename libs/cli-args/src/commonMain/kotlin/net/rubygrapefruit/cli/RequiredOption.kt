package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class RequiredOption<T : Any>(
    matcher: OptionMatcher<T>
) : AbstractOption<T>(matcher), Option<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value!!
    }

    override fun start(context: ParseContext): ParseState {
        return OptionParseState(this, matcher)
    }
}