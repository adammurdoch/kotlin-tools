package net.rubygrapefruit.cli

import kotlin.reflect.KClass

internal class OptionMatcher<T : Any>(
    names: List<String>,
    private val help: String?,
    private val host: Host,
    private val converter: StringConverter<T>
) : Matcher<T> {
    override val markers = names.map { host.marker(it) }

    val type: KClass<*> get() = converter.type

    override fun match(args: List<String>): Matcher.Result<T> {
        val arg = args.first()
        if (!markers.contains(arg)) {
            return Matcher.Nothing()
        }
        if (args.size == 1 || host.isMarker(args[1])) {
            return Matcher.Failure(1, "Value missing for option $arg", expectedMore = true)
        }
        val result = converter.convert("option $arg", args[1])
        return when (result) {
            is StringConverter.Success -> Matcher.Success(2, result.value)
            is StringConverter.Failure -> Matcher.Failure(2, result.message, expectedMore = false)
        }
    }

    override fun usage(): List<OptionUsage> {
        val usage = SingleOptionUsage(markers.joinToString(", ") { "$it <value>" }, help, markers)
        return listOf(OptionUsage(usage.usage, help, converter.type, listOf(usage)))
    }
}