package net.rubygrapefruit.cli

internal abstract class AbstractOption<T : Any>(
    protected val matcher: OptionMatcher<T>,
    protected val help: String?
) : NonPositional {
    protected var value: T? = null

    override fun toString(): String {
        return "${matcher.flags.first()} <value>"
    }

    override fun usage(): List<OptionUsage> {
        val flags = matcher.flags
        val usage = SingleOptionUsage(flags.joinToString(", ") { "$it <value>" }, help, flags)
        return listOf(OptionUsage(usage.usage, help, matcher.type, listOf(usage)))
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
        return matcher.accepts(option)
    }
}