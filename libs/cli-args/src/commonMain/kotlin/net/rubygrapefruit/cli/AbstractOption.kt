package net.rubygrapefruit.cli

internal abstract class AbstractOption<T : Any>(
    protected val names: List<String>,
    protected val help: String?,
    private val host: Host,
    protected val converter: StringConverter<T>
) : NonPositional() {
    private val flags = names.map { host.option(it) }
    protected var value: T? = null

    override fun toString(): String {
        return "${flags.first()} <value>"
    }

    override fun usage(): List<OptionUsage> {
        val usage = SingleOptionUsage(flags.joinToString(", ") { "$it <value>" }, help, flags)
        return listOf(OptionUsage(usage.usage, help, converter.type, listOf(usage)))
    }

    override fun accept(args: List<String>): ParseResult {
        val arg = args.first()
        if (!flags.contains(arg)) {
            return ParseResult.Nothing
        }
        if (args.size == 1 || host.isOption(args[1])) {
            return ParseResult(1, ArgParseException("Value missing for option $arg"), true)
        }
        if (value != null) {
            return ParseResult(2, ArgParseException("Value for option $arg already provided"), true)
        }
        val result = converter.convert("option $arg", args[1])
        if (result.isFailure) {
            return ParseResult(2, result.exceptionOrNull() as ArgParseException, true)
        }
        value = result.getOrThrow()
        return ParseResult.Two
    }
}