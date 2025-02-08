package net.rubygrapefruit.cli

internal abstract class AbstractOption<T : Any>(
    protected val matcher: OptionMatcher<T>
) : Named {
    protected var value: T? = null
        private set

    override val markers: List<String>
        get() = matcher.markers

    override fun toString(): String {
        return "${matcher.markers.first()} <value>"
    }

    override fun usage(): List<OptionUsage> {
        return matcher.usage()
    }

    fun value(value: T?) {
        this.value = value
    }
}