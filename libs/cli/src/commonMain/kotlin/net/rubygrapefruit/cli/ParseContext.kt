package net.rubygrapefruit.cli

internal sealed interface ParseContext {
    val options: List<NonPositional>

    fun withOptions(options: List<NonPositional>): ParseContext
}

internal object RootContext : ParseContext {
    override val options: List<NonPositional>
        get() = emptyList()

    override fun withOptions(options: List<NonPositional>): ParseContext {
        return NestedContext(options)
    }
}

internal class NestedContext(override val options: List<NonPositional>) : ParseContext {
    override fun withOptions(options: List<NonPositional>): ParseContext {
        return NestedContext(options + this.options)
    }
}