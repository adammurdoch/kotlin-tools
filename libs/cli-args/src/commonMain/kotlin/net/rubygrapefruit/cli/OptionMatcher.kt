package net.rubygrapefruit.cli

import kotlin.reflect.KClass

internal class OptionMatcher<T : Any>(
    names: List<String>,
    private val host: Host,
    private val converter: StringConverter<T>
) : Matcher<T> {
    val flags = names.map { host.option(it) }

    val type: KClass<*> get() = converter.type

    override fun match(args: List<String>): Matcher.Result<T> {
        val arg = args.first()
        if (!flags.contains(arg)) {
            return Matcher.Nothing()
        }
        if (args.size == 1 || host.isOption(args[1])) {
            return Matcher.Failure(1, ArgParseException("Value missing for option $arg"), expectedMore = true)
        }
        val result = converter.convert("option $arg", args[1])
        if (result.isFailure) {
            return Matcher.Failure(2, result.exceptionOrNull() as ArgParseException, expectedMore = false)
        }
        return Matcher.Success(2, result.getOrThrow())
    }

    fun accepts(option: String): Boolean {
        return option in flags
    }
}