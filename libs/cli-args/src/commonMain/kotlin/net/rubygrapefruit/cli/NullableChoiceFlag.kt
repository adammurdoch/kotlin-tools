package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class NullableChoiceFlag<T : Any>(
    choices: ChoiceFlagMatcher<T>,
    private val owner: Action
) : AbstractChoiceFlag<T>(choices), NullableOption<T> {

    override fun whenAbsent(default: T): Option<T> {
        return owner.replace(this, OptionalChoiceFlag(matcher, default))
    }

    override fun required(): Option<T> {
        return owner.replace(this, RequiredChoiceFlag(matcher))
    }

    override fun repeated(): ListOption<T> {
        return owner.replace(this, DefaultListOption(matcher))
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }

    fun start(context: ParseContext): ParseState {
        return OptionalChoiceFlagParseState(this, matcher, null)
    }
}
