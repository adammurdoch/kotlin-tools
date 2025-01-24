package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class RequiredChoice<T : Any>(
    choices: List<ChoiceDetails<T>>,
) : AbstractChoice<T>(choices), Option<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value!!
    }

    override fun finished(context: ParseContext): FinishResult {
        return if (value == null) {
            val exception = ArgParseException("One of the following options must be provided: ${choices.joinToString { it.names.maxBy { it.length } }}")
            FinishResult.Failure(exception)
        } else {
            FinishResult.Success
        }
    }
}
