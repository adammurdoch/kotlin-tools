package net.rubygrapefruit.cli

internal abstract class AbstractChoiceFlag<T : Any>(
    protected val matcher: ChoiceFlagMatcher<T>
) : NonPositional {
    protected var value: T? = null

    override fun usage(): List<FlagUsage> {
        return matcher.usage()
    }

    override fun accepts(option: String): Boolean {
        return matcher.accepts(option)
    }

    fun value(value: T?) {
        this.value = value
    }
}
