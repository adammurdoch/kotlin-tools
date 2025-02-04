package net.rubygrapefruit.cli

internal abstract class AbstractChoice<T : Any>(
    protected val matcher: ChoiceFlagMatcher<T>
) : NonPositional {
    protected var value: T? = null

    override fun usage(): List<FlagUsage> {
        return matcher.usage()
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val result = matcher.match(args)
        if (result is Matcher.Success) {
            value = result.value
        }
        return result.asParseResult()
    }

    override fun accepts(option: String): Boolean {
        return matcher.accepts(option)
    }

    fun value(value: T?) {
        this.value = value
    }
}
