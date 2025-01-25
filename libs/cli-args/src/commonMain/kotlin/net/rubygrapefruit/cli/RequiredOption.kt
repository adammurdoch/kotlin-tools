package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class RequiredOption<T : Any>(
    matcher: OptionMatcher<T>
) : AbstractOption<T>(matcher), Option<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value!!
    }

    override fun finished(context: ParseContext): FinishResult {
        return if (value == null) {
            val exception = ArgParseException("Option ${matcher.flags.maxBy { it.length }} not provided")
            FinishResult.Failure(exception, expectedMore = true)
        } else {
            FinishResult.Success
        }
    }
}