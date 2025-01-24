package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class RequiredOption<T : Any>(
    names: List<String>,
    help: String?,
    host: Host,
    converter: StringConverter<T>,
) : AbstractOption<T>(names, help, host, converter), Option<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value!!
    }

    override fun finished(context: ParseContext): FinishResult {
        return if (value == null) {
            val exception = ArgParseException("Option ${flags.maxBy { it.length }} not provided")
            FinishResult.Failure(exception, expectedMore = true)
        } else {
            FinishResult.Success
        }
    }
}