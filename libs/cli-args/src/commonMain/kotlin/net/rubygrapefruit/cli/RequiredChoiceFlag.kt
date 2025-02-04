package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class RequiredChoiceFlag<T : Any>(
    choices: ChoiceFlagMatcher<T>,
) : AbstractChoiceFlag<T>(choices), Option<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException()
    }

    override fun start(context: ParseContext): ParseState {
        return ChoiceFlagParseState(this, matcher)
    }
}
