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

    override fun accepts(option: String): Boolean {
        return matcher.accepts(option)
    }

    fun value(value: T?) {
        this.value = value
    }
}