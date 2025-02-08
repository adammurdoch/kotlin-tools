package net.rubygrapefruit.cli

internal abstract class AbstractChoiceFlag<T : Any>(
    protected val matcher: ChoiceFlagMatcher<T>
) : Named {
    protected var value: T? = null
        private set

    override val markers: List<String>
        get() = matcher.markers

    override fun usage(): List<FlagUsage> {
        return matcher.usage()
    }

    fun value(value: T?) {
        this.value = value
    }
}
