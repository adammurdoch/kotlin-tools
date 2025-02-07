package net.rubygrapefruit.cli

internal abstract class AbstractOption<T : Any>(
    protected val matcher: OptionMatcher<T>
) : Named {
    protected var value: T? = null
        private set

    override fun toString(): String {
        return "${matcher.flags.first()} <value>"
    }

    override fun usage(): List<OptionUsage> {
        return matcher.usage()
    }

    override fun accepts(option: String): Boolean {
        return matcher.accepts(option)
    }

    fun value(value: T?) {
        this.value = value
    }
}