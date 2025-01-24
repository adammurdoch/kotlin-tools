package net.rubygrapefruit.cli

internal abstract class AbstractOption<T : Any>(
    protected val names: List<String>,
    protected val help: String?,
    protected val host: Host,
    protected val converter: StringConverter<T>
) : NonPositional {
    protected val flags = names.map { host.option(it) }
    private val matcher = OptionMatcher(flags, host, converter)
    protected var value: T? = null

    override fun toString(): String {
        return "${flags.first()} <value>"
    }

    override fun usage(): List<OptionUsage> {
        val usage = SingleOptionUsage(flags.joinToString(", ") { "$it <value>" }, help, flags)
        return listOf(OptionUsage(usage.usage, help, converter.type, listOf(usage)))
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val result = matcher.match(args)
        if (result.consumed > 0 && value != null) {
            val arg = args.first()
            return ParseResult.Failure(1, ArgParseException("Value for option $arg already provided"))
        }
        return when (result) {
            is Matcher.Nothing -> ParseResult.Nothing
            is Matcher.Success -> {
                value = result.value
                ParseResult.Success(result.consumed)
            }

            is Matcher.Failure -> {
                ParseResult.Failure(result.consumed, result.failure, result.expectedMore)
            }
        }
    }

    override fun accepts(option: String): Boolean {
        return option in flags
    }
}