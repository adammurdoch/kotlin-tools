package net.rubygrapefruit.cli

internal abstract class AbstractOption<T : Any>(
    protected val matcher: OptionMatcher<T>
) : NonPositional {
    protected var value: T? = null

    override fun toString(): String {
        return "${matcher.flags.first()} <value>"
    }

    override fun usage(): List<OptionUsage> {
        return matcher.usage()
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val result = matcher.match(args)
        if (result.consumed > 0 && value != null) {
            val arg = args.first()
            return ParseResult.Failure(1, ArgParseException("Value for option $arg already provided"))
        }
        if (result is Matcher.Success) {
            value = result.value
        }
        return result.asParseResult()
    }

    override fun accepts(option: String): Boolean {
        return matcher.accepts(option)
    }
}