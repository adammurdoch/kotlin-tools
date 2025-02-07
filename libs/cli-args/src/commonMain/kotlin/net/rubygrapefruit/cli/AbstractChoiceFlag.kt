package net.rubygrapefruit.cli

internal abstract class AbstractChoiceFlag<T : Any>(
    protected val matcher: ChoiceFlagMatcher<T>
) : Named {
    protected var value: T? = null
        private set

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
